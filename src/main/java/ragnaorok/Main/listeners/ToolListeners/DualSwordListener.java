package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ragnaorok.Main.Main;
import ragnaorok.Main.Tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DualSwordListener implements Listener {

    private final Main plugin;
    private final Set<Player> bladeDancePlayers = new HashSet<>();
    private final Map<Player, BukkitRunnable> bladeDanceTasks = new HashMap<>();

    public DualSwordListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        if (Tools.getLoadOut(inventory) == Tools.DUAL_SWORD) {
            if (player.isSneaking() && event.getAction().toString().contains("RIGHT_CLICK")) {
                performBladeDance(player);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (bladeDancePlayers.contains(player) && event.getDamager() instanceof Arrow) {
                event.setCancelled(true);
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
                player.spawnParticle(Particle.CRIT, player.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
            }
        }
    }

    private void performBladeDance(Player player) {
        bladeDancePlayers.add(player);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
        player.sendMessage(ChatColor.RED + "You performed a Blade Dance!");

        BukkitRunnable bladeDanceTask = new BukkitRunnable() {
            int hits = 0;
            boolean mainHand = true;

            @Override
            public void run() {
                if (hits >= 5) {
                    this.cancel();
                    bladeDancePlayers.remove(player);
                    bladeDanceTasks.remove(player);
                    return;
                }
                hits++;

                for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 5, 5, 5)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        LivingEntity target = (LivingEntity) entity;
                        target.damage(4.0, player);
                        Vector knockback = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(0.5);
                        target.setVelocity(knockback);
                        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation(), 10);
                        player.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);
                    }
                }

                player.getWorld().spawnParticle(Particle.CRIT_MAGIC, player.getLocation(), 20, 1, 1, 1, 0.1);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.5f, 1.0f);

                Vector direction = player.getVelocity().setY(0).normalize().multiply(1);
                if (!Double.isFinite(direction.getX()) || !Double.isFinite(direction.getY()) || !Double.isFinite(direction.getZ())) {
                    direction = player.getLocation().getDirection().setY(0).normalize().multiply(1);
                }
                player.setVelocity(direction);

                if (mainHand) {
                    player.swingMainHand();
                } else {
                    player.swingOffHand();
                }
                mainHand = !mainHand;
            }
        };
        bladeDanceTask.runTaskTimer(plugin, 0L, 10L);
        bladeDanceTasks.put(player, bladeDanceTask);
    }

}
