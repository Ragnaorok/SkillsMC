package ragnaorok.Main.skills;

import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.*;
import org.junit.jupiter.api.*;
import ragnaorok.Main.*;
import ragnaorok.Main.listeners.ToolListeners.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeaponListenerTest {
    Player player; PlayerInventory inventory; PlayerInteractEvent event;
    @BeforeEach void setup() {
        player=mock(Player.class);inventory=mock(PlayerInventory.class);event=mock(PlayerInteractEvent.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());when(player.getInventory()).thenReturn(inventory);
        when(player.getLocation()).thenReturn(new Location(null,0,0,0));
        when(event.getPlayer()).thenReturn(player);when(event.getHand()).thenReturn(EquipmentSlot.HAND);
        when(event.getAction()).thenReturn(Action.LEFT_CLICK_AIR);
        Constant.SKILLS_MC_PLAYER_HASH_MAP.put(player.getUniqueId().toString(),new SkillsMCPlayer(player,10,10,0,0,0,ClassType.NONE));
        doReturn(item(Material.AIR)).when(inventory).getItemInOffHand();
    }
    ItemStack item(Material type) { var item=mock(ItemStack.class);when(item.getType()).thenReturn(type);return item; }
    void equip(Material type) { var stack=item(type);when(inventory.getItemInMainHand()).thenReturn(stack);when(player.getItemInHand()).thenReturn(stack); }
    @AfterEach void clear() { ragnaorok.Main.progression.MageMana.clear(); SkillAccess.shutdown();Constant.SKILLS_MC_PLAYER_HASH_MAP.clear(); }
    @Test void unauthorizedWeaponInputsLeaveBaseInteractionUntouched() {
        equip(Material.BOW);new BowListener(null).onPlayerInteract(event);
        equip(Material.CROSSBOW);new CrossBowListener().onBlock(event);
        equip(Material.BLAZE_ROD);new MageListener(null).onCast(event);
        equip(Material.IRON_HOE);when(event.getAction()).thenReturn(Action.RIGHT_CLICK_AIR);new HoeListener(null).onHoeUse(event);
        equip(Material.IRON_SWORD);doReturn(item(Material.IRON_SWORD)).when(inventory).getItemInOffHand();when(player.isSneaking()).thenReturn(true);
        new DualSwordListener(null).onPlayerInteract(event);
        verify(event,never()).setCancelled(anyBoolean());verify(player,never()).getWorld();
        verify(player,never()).setVelocity(any());verify(player,never()).addPotionEffect(any());
    }
    @Test void ordinaryBowShotIsNeverEnhancedOrCancelledWithoutPreparedCast() {
        var shot=mock(EntityShootBowEvent.class);var arrow=mock(Arrow.class);
        when(shot.getEntity()).thenReturn(player);doReturn(item(Material.BOW)).when(shot).getBow();when(shot.getProjectile()).thenReturn(arrow);
        new BowListener(null).onEntityShootBow(shot);
        verifyNoInteractions(arrow);verify(shot,never()).setCancelled(anyBoolean());
    }
    @Test void preparedTrueshotCannotSurviveClassSwitchBack() {
        var profile=Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString());
        profile.setClassType(ClassType.ARCHER);when(player.isOnline()).thenReturn(true);
        equip(Material.BOW);when(player.isSneaking()).thenReturn(true);
        var listener=new BowListener(null);listener.onPlayerInteract(event);
        verify(player).sendMessage(org.bukkit.ChatColor.GREEN + "Trueshot ready for the next shot");
        profile.setClassType(ClassType.WARRIOR);SkillAccess.invalidate(player);
        profile.setClassType(ClassType.ARCHER);
        var shot=mock(EntityShootBowEvent.class);var arrow=mock(Arrow.class);
        when(shot.getEntity()).thenReturn(player);doReturn(item(Material.BOW)).when(shot).getBow();when(shot.getProjectile()).thenReturn(arrow);
        listener.onEntityShootBow(shot);
        verifyNoInteractions(arrow);verify(shot,never()).setCancelled(anyBoolean());
    }
    @Test void wrongClassMeleeKeepsOrdinaryDamageWithoutSpecialKnockback() {
        var damage=mock(org.bukkit.event.entity.EntityDamageByEntityEvent.class);
        var target=mock(Monster.class);
        when(damage.getDamager()).thenReturn(player);when(damage.getEntity()).thenReturn(target);
        when(player.isSneaking()).thenReturn(true);
        equip(Material.IRON_SWORD);new SwordListener(null).onSwordAttack(damage);
        equip(Material.IRON_AXE);new AxeListener().onAxeHit(damage);
        verify(damage,never()).setCancelled(anyBoolean());verify(damage,never()).setDamage(anyDouble());
        verify(target,never()).setVelocity(any());verify(player,never()).getWorld();
    }
    @Test void wrongClassShieldDoesNotCancelFallDamage() {
        equip(Material.SHIELD);when(player.isSneaking()).thenReturn(true);when(player.isBlocking()).thenReturn(true);
        var damage=mock(org.bukkit.event.entity.EntityDamageEvent.class);
        when(damage.getEntity()).thenReturn(player);when(damage.getCause()).thenReturn(org.bukkit.event.entity.EntityDamageEvent.DamageCause.FALL);
        new ShieldListener().onPlayerLand(damage);
        verify(damage,never()).setCancelled(anyBoolean());verify(player,never()).setVelocity(any());
    }
    @Test void offhandInputCannotCastEvenForOwner() {
        Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString()).setClassType(ClassType.MAGE);
        equip(Material.WOODEN_HOE);when(event.getHand()).thenReturn(EquipmentSlot.OFF_HAND);
        new MageListener(null).onCast(event);verify(player,never()).getWorld();verify(event,never()).setCancelled(anyBoolean());
    }
    @Test void blazeRodNoLongerCastsForMage() {
        Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString()).setClassType(ClassType.MAGE);
        equip(Material.BLAZE_ROD);
        new MageListener(null).onCast(event);
        verify(event,never()).setCancelled(anyBoolean());
        verify(player,never()).getWorld();
        assertEquals(Loadout.NONE, Loadout.getMainHand(inventory));
    }
    @Test void allHoeTiersRecognizedAndClericCannotUseMageCasting() {
        for (Material material : Material.values()) {
            if (material.name().endsWith("_HOE")) {
                assertTrue(MageListener.isHoe(material));
                equip(material);
                assertEquals(Loadout.HOE, Loadout.getMainHand(inventory));
            }
        }
        Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString()).setClassType(ClassType.CLERIC);
        new MageListener(null).onCast(event);
        verify(event,never()).setCancelled(anyBoolean());
        verify(player,never()).getWorld();
    }
    @Test void mageCanSelectSpellWithoutCastingOrClericEffects() {
        var profile = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString());
        profile.setClassType(ClassType.MAGE);
        ragnaorok.Main.progression.MageMana.prepare(profile);
        when(player.spigot()).thenReturn(mock(Player.Spigot.class));
        equip(Material.WOODEN_HOE);
        when(event.getAction()).thenReturn(Action.RIGHT_CLICK_BLOCK);
        when(player.isSneaking()).thenReturn(true);
        new MageListener(null).onCast(event);
        new HoeListener(null).onHoeUse(event);
        assertEquals(ragnaorok.Main.progression.MageMana.Spell.ERUPTION,
                ragnaorok.Main.progression.MageMana.selected(player.getUniqueId()));
        assertEquals(100, profile.getMana());
        verify(event).setCancelled(true);
        verify(player,never()).getWorld();
    }
    @Test void arcaneBoltReplacesNativeDamageWithOneScaledHit() {
        Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString()).setClassType(ClassType.MAGE);
        var bolt=mock(Snowball.class);
        var target=mock(Blaze.class);
        var data=mock(org.bukkit.persistence.PersistentDataContainer.class);
        when(bolt.getPersistentDataContainer()).thenReturn(data);
        when(data.get(new org.bukkit.NamespacedKey("skillsmc", "arcane_bolt"),
                org.bukkit.persistence.PersistentDataType.DOUBLE)).thenReturn(3.09);
        when(bolt.getShooter()).thenReturn(player);
        var hit=mock(org.bukkit.event.entity.ProjectileHitEvent.class);
        when(hit.getEntity()).thenReturn(bolt);when(hit.getHitEntity()).thenReturn(target);
        new MageListener(null).boltHit(hit);
        verify(target).damage(3.09,player);
        verify(hit).setCancelled(true);
        verify(bolt).remove();
    }
}
