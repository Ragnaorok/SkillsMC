package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.bukkit.scheduler.BukkitRunnable;
import ragnaorok.Main.Loadout;
import ragnaorok.Main.Main;

public class SwordListener implements Listener {

    private final Main plugin;

    public SwordListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwordAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            PlayerInventory inventory = player.getInventory();

            if (Loadout.getLoadOut(inventory) == Loadout.SWORD) {
                if (player.isSneaking()) {
                    // Apply Gale Slash effects
                    Entity target = event.getEntity();
                    Vector direction = player.getLocation().getDirection().normalize().multiply(2);
                    direction.setY(0.5);
                    target.setVelocity(direction);

                    // Play sound effect
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);

                    // Create particle effects for the helix
                    new BukkitRunnable() {
                        double t = 0;
                        @Override
                        public void run() {
                            if (t > 2 * Math.PI) {
                                this.cancel();
                                return;
                            }
                            double x = 0.5 * t * Math.cos(t);
                            double y = 0.5 * t;
                            double z = 0.5 * t * Math.sin(t);
                            target.getWorld().spawnParticle(Particle.CLOUD, target.getLocation().add(x, y, z), 1, 0, 0, 0, 0.01);
                            target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(x, y, z), 1, 0, 0, 0, 0.01);
                            t += Math.PI / 8;
                        }
                    }.runTaskTimer(plugin, 0L, 1L);

                    // Apply spinning effect
                    new BukkitRunnable() {
                        int ticks = 0;
                        @Override
                        public void run() {
                            if (ticks >= 20) {
                                this.cancel();
                                return;
                            }
                            Vector velocity = target.getVelocity();
                            double newX = velocity.getX() * Math.cos(Math.PI / 10) - velocity.getZ() * Math.sin(Math.PI / 10);
                            double newZ = velocity.getX() * Math.sin(Math.PI / 10) + velocity.getZ() * Math.cos(Math.PI / 10);
                            velocity.setX(newX);
                            velocity.setZ(newZ);
                            target.setVelocity(velocity);
                            ticks++;
                        }
                    }.runTaskTimer(plugin, 0L, 1L);

                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation(), 10, 1.0, 1.0, 1.0, 0.1);

                    player.sendMessage(ChatColor.AQUA + "You performed a Gale Slash!");
                }
            }
        }
    }
}
