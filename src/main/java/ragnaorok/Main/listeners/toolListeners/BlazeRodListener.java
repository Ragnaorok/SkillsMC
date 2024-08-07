package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ragnaorok.Main.Main;

public class BlazeRodListener implements Listener {

    private final Main plugin;

    public BlazeRodListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCast(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (player.isSneaking()) {
                    castLavaEruption(player);
                } else {
                    castFireball(player);
                }
            }
        }
    }

    private void castFireball(Player player) {
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection();
        Location fireballSpawnLocation = location.add(direction.multiply(2));

        Fireball fireball = player.getWorld().spawn(fireballSpawnLocation, Fireball.class);
        fireball.setDirection(direction);
        fireball.setYield(0);  // Ensure the fireball doesn't destroy blocks
        fireball.setIsIncendiary(false);

        player.getWorld().playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.0f, 1.0f);
        player.sendMessage(ChatColor.RED + "You cast a fireball!");

        addParticleEffectsToFireball(fireball);
    }

    private void addParticleEffectsToFireball(Fireball fireball) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (fireball.isDead() || !fireball.isValid()) {
                    this.cancel();
                    return;
                }
                Location loc = fireball.getLocation();
                fireball.getWorld().spawnParticle(Particle.SPELL, loc, 5, 0.5, 0.5, 0.5, 0.05);
                fireball.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc, 5, 0.5, 0.5, 0.5, 0.05);
                fireball.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 5, 0.2, 0.2, 0.2, 0.01);
                fireball.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 0.5f, 1.0f);
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void castLavaEruption(Player player) {
        Location location = player.getLocation();
        player.sendMessage(ChatColor.RED + "Lava Eruption!");

        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
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
            lavaLoc.getWorld().spawnParticle(Particle.SMOKE_LARGE, lavaLoc, 20, 0.5, 0.5, 0.5, 0.1);
            damageNearbyEntities(lavaLoc, player, 10.0);
        }
    }
    private void damageNearbyEntities(Location location, Player player, double radius) {
        for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
            if (entity instanceof Monster && entity != player) {
                ((LivingEntity) entity).damage(2.0, player);
                entity.setFireTicks(20);
            }
        }
    }
}