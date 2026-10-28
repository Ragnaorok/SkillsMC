package ragnaorok.Main.progression;

import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.*;
import org.junit.jupiter.api.*;
import ragnaorok.Main.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MobLevelsTest {
    @AfterEach void clear() { Constant.SKILLS_MC_PLAYER_HASH_MAP.clear(); }
    @Test void activityAndGameModeExcludeInactiveCreativeAndSpectators() {
        var player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.isOnline()).thenReturn(true);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        Constant.SKILLS_MC_PLAYER_HASH_MAP.put(player.getUniqueId().toString(),
                new SkillsMCPlayer(player, 10, 10, 0, 0, 0, ClassType.ARCHER));
        var levels = new MobLevels();
        assertFalse(levels.eligible(player, System.currentTimeMillis()));
        levels.activate(player);
        assertTrue(levels.eligible(player, System.currentTimeMillis()));
        assertFalse(levels.eligible(player, System.currentTimeMillis() + MobLevels.ACTIVE_MILLIS + 1));
        when(player.getGameMode()).thenReturn(GameMode.CREATIVE);
        assertFalse(levels.eligible(player, System.currentTimeMillis()));
        when(player.getGameMode()).thenReturn(GameMode.SPECTATOR);
        assertFalse(levels.eligible(player, System.currentTimeMillis()));
        when(player.getGameMode()).thenReturn(GameMode.ADVENTURE);
        assertTrue(levels.eligible(player, System.currentTimeMillis()));
        when(player.isDead()).thenReturn(true);
        assertFalse(levels.eligible(player, System.currentTimeMillis()));
    }
    @Test void passiveNeutralAndBossMobsAreExcluded() {
        for (var type : new EntityType[]{EntityType.COW, EntityType.VILLAGER, EntityType.ENDERMAN,
                EntityType.PIGLIN, EntityType.ZOMBIFIED_PIGLIN, EntityType.WITHER,
                EntityType.ENDER_DRAGON, EntityType.WARDEN, EntityType.ELDER_GUARDIAN}) {
            assertFalse(MobLevels.HOSTILES.contains(type));
        }
        assertTrue(MobLevels.HOSTILES.contains(EntityType.ZOMBIE));
        assertFalse(MobLevels.ARMORED.contains(EntityType.CREEPER));
    }
    @Test void assignedMobsAreNotRerolledOnSpawn() {
        var event = mock(CreatureSpawnEvent.class);
        var zombie = mock(Zombie.class);
        var data = mock(PersistentDataContainer.class);
        when(event.getEntity()).thenReturn(zombie);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);
        when(zombie.getPersistentDataContainer()).thenReturn(data);
        when(data.get(MobLevels.LEVEL, PersistentDataType.INTEGER)).thenReturn(10);
        new MobLevels().spawn(event);
        verify(zombie, never()).getLocation();
        verify(zombie, never()).getEquipment();
    }
    @Test void markedMobDamageScalesWithoutChangingVanillaAttacks() {
        var mob = mock(Zombie.class);
        var data = mock(PersistentDataContainer.class);
        when(mob.getPersistentDataContainer()).thenReturn(data);
        var event = mock(EntityDamageByEntityEvent.class);
        when(event.getDamager()).thenReturn(mob);
        when(event.getDamage()).thenReturn(4.0);
        var listener = new MobLevels();
        listener.damage(event);
        verify(event, never()).setDamage(anyDouble());
        when(data.get(MobLevels.LEVEL, PersistentDataType.INTEGER)).thenReturn(10);
        listener.damage(event);
        verify(event).setDamage(4 * 1.09);
    }
    @Test void mobProjectilesSnapshotLevelAtLaunch() {
        var mob = mock(Skeleton.class);
        var mobData = mock(PersistentDataContainer.class);
        when(mob.getPersistentDataContainer()).thenReturn(mobData);
        when(mobData.get(MobLevels.LEVEL, PersistentDataType.INTEGER)).thenReturn(20);
        var arrow = mock(Arrow.class);
        var arrowData = mock(PersistentDataContainer.class);
        when(arrow.getShooter()).thenReturn(mob);
        when(arrow.getPersistentDataContainer()).thenReturn(arrowData);
        var event = mock(ProjectileLaunchEvent.class);
        when(event.getEntity()).thenReturn(arrow);
        new MobLevels().launch(event);
        verify(arrowData).set(MobLevels.PROJECTILE_LEVEL, PersistentDataType.INTEGER, 20);
    }

    @Test void nearbyGroupUsesCylinderAndLowestLevelCap() {
        var world = mock(org.bukkit.World.class);
        var origin = new org.bukkit.Location(world, 0, 64, 0);
        var levels = new MobLevels();
        Player low = nearby(world, levels, 5, 10, 64, 0);
        Player high = nearby(world, levels, 45, 20, 64, 0);
        Player far = nearby(world, levels, 1, 128, 64, 128);
        Player above = nearby(world, levels, 1, 0, 113, 0);
        when(world.getNearbyPlayers(origin, 128, 48)).thenReturn(java.util.List.of(low, high, far, above));
        assertEquals(10, levels.nearbyLevel(origin, 2));
        when(world.getNearbyPlayers(origin, 128, 48)).thenReturn(java.util.List.of(far, above));
        assertEquals(1, levels.nearbyLevel(origin, 2));
    }
    private Player nearby(org.bukkit.World world, MobLevels levels, int level, double x, double y, double z) {
        var player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.isOnline()).thenReturn(true);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        when(player.getLocation()).thenReturn(new org.bukkit.Location(world, x, y, z));
        var profile = new SkillsMCPlayer(player, 10, 10, 0, 0, 0, ClassType.WARRIOR);
        profile.setLevel(level);
        Constant.SKILLS_MC_PLAYER_HASH_MAP.put(player.getUniqueId().toString(), profile);
        levels.activate(player);
        return player;
    }

    @Test void generatedArmorIsRemovedFromDropsButOrdinaryGearRemains() {
        var generated = mock(org.bukkit.inventory.ItemStack.class);
        var ordinary = mock(org.bukkit.inventory.ItemStack.class);
        var meta = mock(org.bukkit.inventory.meta.ItemMeta.class);
        var data = mock(PersistentDataContainer.class);
        when(generated.hasItemMeta()).thenReturn(true);
        when(generated.getItemMeta()).thenReturn(meta);
        when(meta.getPersistentDataContainer()).thenReturn(data);
        when(data.has(MobLevels.ARMOR, PersistentDataType.BYTE)).thenReturn(true);
        var drops = new java.util.ArrayList<>(java.util.List.of(generated, ordinary));
        var event = mock(EntityDeathEvent.class);
        when(event.getDrops()).thenReturn(drops);
        new MobLevels().drops(event);
        assertEquals(java.util.List.of(ordinary), drops);
    }
    @Test void existingArmorIsNeverReplaced() {
        var mob = mock(Zombie.class);
        var data = mock(PersistentDataContainer.class);
        var equipment = mock(org.bukkit.inventory.EntityEquipment.class);
        var existing = mock(org.bukkit.inventory.ItemStack.class);
        when(existing.getType()).thenReturn(org.bukkit.Material.DIAMOND_CHESTPLATE);
        when(mob.getType()).thenReturn(EntityType.ZOMBIE);
        when(mob.getPersistentDataContainer()).thenReturn(data);
        when(data.get(MobLevels.LEVEL, PersistentDataType.INTEGER)).thenReturn(35);
        when(mob.getEquipment()).thenReturn(equipment);
        when(equipment.getItem(any(org.bukkit.inventory.EquipmentSlot.class))).thenReturn(existing);
        new MobLevels().equip(mob);
        verify(equipment, never()).setItem(any(org.bukkit.inventory.EquipmentSlot.class), any());
        verify(equipment, never()).setDropChance(any(), anyFloat());
    }
}
