package ragnaorok.Main.progression;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.*;
import org.bukkit.attribute.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;
import ragnaorok.Main.SkillsMCPlayer;

/** Main-thread, spawn-time scaling only. No repeating mob scans or chunk loads. */
public final class MobLevels implements Listener {
    static final NamespacedKey LEVEL = new NamespacedKey("skillsmc", "mob_level");
    static final NamespacedKey HEALTH = new NamespacedKey("skillsmc", "mob_health");
    static final NamespacedKey ARMOR = new NamespacedKey("skillsmc", "mob_armor");
    static final NamespacedKey PROJECTILE_LEVEL = new NamespacedKey("skillsmc", "mob_projectile_level");
    static final long ACTIVE_MILLIS = 5 * 60 * 1000L;
    private final Map<UUID, Long> activity = new HashMap<>();
    // Deliberate allowlist: no neutral piglins/endermen, passive animals or bosses.
    static final Set<EntityType> HOSTILES = EnumSet.of(
            EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER, EntityType.HUSK, EntityType.DROWNED,
            EntityType.SKELETON, EntityType.STRAY, EntityType.BOGGED, EntityType.WITHER_SKELETON,
            EntityType.CREEPER, EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.SILVERFISH,
            EntityType.ENDERMITE, EntityType.SLIME, EntityType.MAGMA_CUBE, EntityType.PHANTOM,
            EntityType.BLAZE, EntityType.BREEZE, EntityType.GHAST, EntityType.GUARDIAN,
            EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.EVOKER, EntityType.VEX,
            EntityType.WITCH, EntityType.RAVAGER, EntityType.PIGLIN_BRUTE, EntityType.HOGLIN,
            EntityType.ZOGLIN, EntityType.SHULKER);
    static final Set<EntityType> ARMORED = EnumSet.of(EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER,
            EntityType.HUSK, EntityType.DROWNED, EntityType.SKELETON, EntityType.STRAY,
            EntityType.BOGGED, EntityType.WITHER_SKELETON);
    public void activate(Player player) { activity.put(player.getUniqueId(), System.currentTimeMillis()); }
    public boolean eligible(Player player, long now) {
        return player.isOnline() && !player.isDead()
                && (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)
                && PlayerStats.profile(player) != null
                && now - activity.getOrDefault(player.getUniqueId(), 0L) <= ACTIVE_MILLIS;
    }
    @EventHandler public void join(PlayerJoinEvent event) { activate(event.getPlayer()); }
    @EventHandler public void quit(PlayerQuitEvent event) { activity.remove(event.getPlayer().getUniqueId()); }
    @EventHandler(ignoreCancelled = true) public void move(PlayerMoveEvent event) {
        Location a = event.getFrom(), b = event.getTo();
        if (b != null && (a.getX() != b.getX() || a.getY() != b.getY() || a.getZ() != b.getZ())) activate(event.getPlayer());
    }
    @EventHandler public void interact(PlayerInteractEvent event) { activate(event.getPlayer()); }
    @EventHandler(ignoreCancelled = true) public void breakBlock(org.bukkit.event.block.BlockBreakEvent event) { activate(event.getPlayer()); }
    @EventHandler(ignoreCancelled = true) public void placeBlock(org.bukkit.event.block.BlockPlaceEvent event) { activate(event.getPlayer()); }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void spawn(CreatureSpawnEvent event) {
        LivingEntity mob = event.getEntity();
        if (!HOSTILES.contains(mob.getType()) || level(mob) != null) return;
        // Transformations retain their existing assignment through transform(), not a fresh local roll.
        if (EnumSet.of(CreatureSpawnEvent.SpawnReason.DROWNED, CreatureSpawnEvent.SpawnReason.INFECTION,
                CreatureSpawnEvent.SpawnReason.CURED, CreatureSpawnEvent.SpawnReason.FROZEN).contains(event.getSpawnReason())) return;
        assign(mob, nearbyLevel(mob.getLocation(), ThreadLocalRandom.current().nextInt(-2, 3)));
        equip(mob);
    }
    int nearbyLevel(Location origin, int variation) {
        List<Integer> levels = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (Player player : origin.getWorld().getNearbyPlayers(origin, 128, 48)) {
            Location at = player.getLocation();
            double dx = at.getX() - origin.getX(), dz = at.getZ() - origin.getZ();
            if (dx * dx + dz * dz > 128 * 128 || Math.abs(at.getY() - origin.getY()) > 48 || !eligible(player, now)) continue;
            SkillsMCPlayer profile = PlayerStats.profile(player);
            levels.add(profile.getLevel());
        }
        return MobScaling.level(levels, variation);
    }
    public static Integer level(Entity entity) { return entity.getPersistentDataContainer().get(LEVEL, PersistentDataType.INTEGER); }

    void assign(LivingEntity mob, int level) {
        level = Math.clamp(level, 1, MobScaling.MAX_LEVEL);
        mob.getPersistentDataContainer().set(LEVEL, PersistentDataType.INTEGER, level);
        AttributeInstance health = mob.getAttribute(Attribute.MAX_HEALTH);
        if (health != null) {
            double fraction = mob.getHealth() / Math.max(1, health.getValue());
            for (AttributeModifier modifier : new ArrayList<>(health.getModifiers()))
                if (modifier.getKey().equals(HEALTH)) health.removeModifier(modifier);
            health.addModifier(new AttributeModifier(HEALTH, MobScaling.healthMultiplier(level) - 1,
                    AttributeModifier.Operation.ADD_SCALAR));
            mob.setHealth(Math.clamp(fraction * health.getValue(), 0, health.getValue()));
        }

    }
    private static String title(EntityType type) {
        String[] words = type.name().toLowerCase(Locale.ROOT).split("_");
        for (int i = 0; i < words.length; i++) words[i] = Character.toUpperCase(words[i].charAt(0)) + words[i].substring(1);
        return String.join(" ", words);
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void transform(EntityTransformEvent event) {
        Integer oldLevel = level(event.getEntity());
        if (oldLevel == null) return;
        for (Entity target : event.getTransformedEntities()) {
            if (target instanceof LivingEntity living && HOSTILES.contains(target.getType())) assign(living, oldLevel);
        }
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void launch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Entity shooter) {
            Integer level = level(shooter);
            if (level != null) event.getEntity().getPersistentDataContainer().set(PROJECTILE_LEVEL, PersistentDataType.INTEGER, level);
        }
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void damage(EntityDamageByEntityEvent event) {
        Entity source = event.getDamager();
        Player attacker = source instanceof Player player ? player
                : source instanceof Projectile projectile && projectile.getShooter() instanceof Player player ? player : null;
        if (attacker != null) {
            activate(attacker);
            Integer targetLevel = level(event.getEntity());
            if (targetLevel != null) attacker.sendActionBar(net.kyori.adventure.text.Component.text(
                    "[Lv. " + targetLevel + "] " + title(event.getEntity().getType())));
        }
        Integer level = level(source);
        if (source instanceof Projectile) level = source.getPersistentDataContainer().get(PROJECTILE_LEVEL, PersistentDataType.INTEGER);
        if (level != null) event.setDamage(event.getDamage() * MobScaling.damageMultiplier(level));
    }
    void equip(LivingEntity mob) {
        if (!ARMORED.contains(mob.getType())) return;
        EntityEquipment equipment = mob.getEquipment();
        Integer level = level(mob);
        if (equipment == null || level == null) return;
        String tier = level >= 20 ? "IRON" : level >= 10 ? "CHAINMAIL" : "LEATHER";
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        String[] pieces = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"};
        for (int i = 0; i < slots.length; i++) {
            // Preserve ALL existing equipment, including enchants/custom items.
            ItemStack existing = equipment.getItem(slots[i]);
            if (existing != null && existing.getType() != Material.AIR) continue;
            if (ThreadLocalRandom.current().nextDouble() >= MobScaling.armorChance(level)) continue;
            ItemStack item = new ItemStack(Material.valueOf(tier + "_" + pieces[i]));
            var meta = item.getItemMeta();
            meta.getPersistentDataContainer().set(ARMOR, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
            equipment.setItem(slots[i], item);
            equipment.setDropChance(slots[i], 0);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void drops(EntityDeathEvent event) {
        event.getDrops().removeIf(item -> item.hasItemMeta()
                && item.getItemMeta().getPersistentDataContainer().has(ARMOR, PersistentDataType.BYTE));
    }
}
