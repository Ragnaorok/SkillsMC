package ragnaorok.Main.listeners.ToolListeners;
import ragnaorok.Main.skills.SkillAccess;
import ragnaorok.Main.progression.PlayerStats;
import static ragnaorok.Main.skills.SkillAccess.WeaponSkill.MAGE_HOE;


import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ragnaorok.Main.Main;

public class MageListener implements Listener {

    private final Main plugin;

    public MageListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCast(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) return;
        if (!isHoe(player.getInventory().getItemInMainHand().getType())
                || !SkillAccess.allows(player, MAGE_HOE)) return;
        Action action = event.getAction();
        boolean right = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
        boolean left = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
        if (!right && !left) return;
        event.setCancelled(true); // Casting must not till soil.
        var id = player.getUniqueId();
        if (right && player.isSneaking()) {
            ragnaorok.Main.progression.MageMana.cycle(id);
        } else {
            var spell = left ? ragnaorok.Main.progression.MageMana.Spell.BOLT
                    : ragnaorok.Main.progression.MageMana.selected(id);
            String error = ragnaorok.Main.progression.MageMana.cast(id, PlayerStats.profile(player), spell,
                    System.currentTimeMillis(), () -> {
                        switch (spell) {
                            case BOLT -> castBolt(player);
                            case FIREBALL -> castFireball(player);
                            case ERUPTION -> castLavaEruption(player);
                        }
                    });
            if (error != null) {
                ragnaorok.Main.ActionBarUtil.sendActionBar(player, error);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, .3f, .7f);
                return;
            }
        }
        ragnaorok.Main.ActionBarUtil.sendActionBar(player,
                ragnaorok.Main.progression.MageMana.display(id, PlayerStats.profile(player)));
    }

    public static boolean isHoe(Material material) {
        return material.name().endsWith("_HOE");
    }

    private void castBolt(Player player) {
        var session = SkillAccess.session(player, MAGE_HOE);
        Snowball bolt = player.launchProjectile(Snowball.class);
        bolt.setVelocity(player.getEyeLocation().getDirection().multiply(1.5));
        bolt.getPersistentDataContainer().set(BOLT_DAMAGE, org.bukkit.persistence.PersistentDataType.DOUBLE,
                PlayerStats.damage(player, 3));
        session.track(bolt);
        new BukkitRunnable() {
            int ticks;
            public void run() {
                if (!session.valid(MAGE_HOE) || !bolt.isValid() || bolt.isDead() || ticks++ >= 40) {
                    bolt.remove(); cancel(); return;
                }
                bolt.getWorld().spawnParticle(Particle.END_ROD, bolt.getLocation(), 1);
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    private static final NamespacedKey BOLT_DAMAGE = new NamespacedKey("skillsmc", "arcane_bolt");
    @EventHandler(ignoreCancelled = true)
    public void boltHit(org.bukkit.event.entity.ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball bolt)) return;
        Double damage = bolt.getPersistentDataContainer().get(BOLT_DAMAGE, org.bukkit.persistence.PersistentDataType.DOUBLE);
        if (damage == null) return;
        event.setCancelled(true); // Replace native snowball damage, including its bonus against blazes.
        if (bolt.getShooter() instanceof Player player && SkillAccess.allows(player, MAGE_HOE)
                && event.getHitEntity() instanceof LivingEntity target && target != player) target.damage(damage, player);
        bolt.remove();
    }

    @EventHandler
    public void respawn(org.bukkit.event.player.PlayerRespawnEvent event) {
        var profile = PlayerStats.profile(event.getPlayer());
        if (profile != null && profile.getClassType() == ragnaorok.Main.ClassType.MAGE) {
            ragnaorok.Main.progression.MageMana.prepare(profile);
            profile.setMana(profile.getMaxMana());
        }
    }

    private void castFireball(Player player) {
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection();
        Location fireballSpawnLocation = location.add(direction.multiply(2));

        Fireball fireball = player.getWorld().spawn(fireballSpawnLocation, Fireball.class);
        fireball.setDirection(direction);
        fireball.setShooter(player);
        ragnaorok.Main.progression.SkillScaling.markDamage(fireball, PlayerStats.damage(player, 1));
        fireball.setYield(0);  // Ensure the fireball doesn't destroy blocks
        fireball.setIsIncendiary(false);

        player.getWorld().playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.0f, 1.0f);


        var session = SkillAccess.session(player, MAGE_HOE);
        session.track(fireball);
        addParticleEffectsToFireball(fireball, session);
    }

    private void addParticleEffectsToFireball(Fireball fireball, SkillAccess.Session session) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!session.valid(MAGE_HOE)) { cancel(); return; }
                if (fireball.isDead() || !fireball.isValid()) {
                    this.cancel();
                    return;
                }
                Location loc = fireball.getLocation();
                fireball.getWorld().spawnParticle(Particle.EFFECT, loc, 5, 0.5, 0.5, 0.5, 0.05, new Particle.Spell(org.bukkit.Color.WHITE, 1.0f));
                fireball.getWorld().spawnParticle(Particle.ENCHANT, loc, 5, 0.5, 0.5, 0.5, 0.05);
                fireball.getWorld().spawnParticle(Particle.LARGE_SMOKE, loc, 5, 0.2, 0.2, 0.2, 0.01);
                fireball.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 1.0f);
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void castLavaEruption(Player player) {
        var session = SkillAccess.session(player, MAGE_HOE);
        Location location = player.getLocation();


        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (!session.valid(MAGE_HOE)) { cancel(); return; }
                if (count >= 3) {
                    this.cancel();
                    return;
                }
                count++;
                eruptLava(player, location);
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void eruptLava(Player player, Location location) {
        for (int i = 0; i < 10; i++) {
            Location lavaLoc = location.clone().add((Math.random() - 0.5) * 10, 0, (Math.random() - 0.5) * 10);
            lavaLoc.getWorld().spawnParticle(Particle.LAVA, lavaLoc, 20, 0.5, 0.5, 0.5, 0.1);
            lavaLoc.getWorld().playSound(lavaLoc, Sound.BLOCK_LAVA_POP, 1.0f, 1.0f);
            lavaLoc.getWorld().spawnParticle(Particle.FLAME, lavaLoc, 20, 0.5, 0.5, 0.5, 0.1);
            lavaLoc.getWorld().spawnParticle(Particle.LARGE_SMOKE, lavaLoc, 20, 0.5, 0.5, 0.5, 0.1);
            damageNearbyEntities(lavaLoc, player, 10.0);
        }
    }
    private void damageNearbyEntities(Location location, Player player, double radius) {
        for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (entity instanceof Monster && entity != player) {
                ((LivingEntity) entity).damage(PlayerStats.damage(player, 2.0), player);
                entity.setFireTicks(20);
            }
        }
    }
}