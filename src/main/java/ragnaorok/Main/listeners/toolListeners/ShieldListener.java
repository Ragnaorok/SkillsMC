package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import java.util.Random;
public class ShieldListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        Location loc = player.getLocation();
        double yaw = loc.getYaw();
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.SILVER, 1);

        if ((mainHandItem == null || mainHandItem.getType() != Material.SHIELD) &&
                (offHandItem == null || offHandItem.getType() != Material.SHIELD)) {
            return;
        }
 /* this is a particle swing effect test. disabled
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            for (double i = 100; i <= 130; i += 10) { // right to left quarter circle
                double angle = (i + yaw) * Math.PI / 180;
                double x1 = loc.getX() + 2 * Math.cos(angle);
                double z1 = loc.getZ() + 2 * Math.sin(angle);
                double y1 = loc.getY() + 1 + 0.4 * Math.sin(angle);
                player.spawnParticle(Particle.REDSTONE, x1, y1, z1, 5, dustOptions);
            }
            player.sendMessage("left swing");
        }
        */
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        ItemStack shield = null;

        if (mainHandItem != null && mainHandItem.getType() == Material.SHIELD) {
            shield = mainHandItem;
        } else if (offHandItem != null && offHandItem.getType() == Material.SHIELD) {
            shield = offHandItem;
        } else {
            return;
        }

        // Check if player is holding a shield, crouching, and falling
        if (player.isSneaking() && player.isBlocking() && player.getVelocity().getY() < -0.5) {
            // Disable shield surfing if shield has 1 durability left
            if (shield.getDurability() >= shield.getType().getMaxDurability() - 1) {
                player.sendMessage(ChatColor.RED + "Your shield is too damaged to surf!");
                return;
            }

            // Apply falling effect
            Vector velocity = player.getVelocity();
            if (!player.isOnGround()) {
                player.setVelocity(velocity);
            }
        }
    }

    @EventHandler
    public void onPlayerLand(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack mainHandItem = player.getInventory().getItemInMainHand();
            ItemStack offHandItem = player.getInventory().getItemInOffHand();
            ItemStack shield = null;

            if (mainHandItem != null && mainHandItem.getType() == Material.SHIELD) {
                shield = mainHandItem;
            } else if (offHandItem != null && offHandItem.getType() == Material.SHIELD) {
                shield = offHandItem;
            } else {
                return;
            }

            // Check if the player is landing from a fall
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && player.isSneaking() && player.isBlocking()) {
                // Cancel fall damage
                event.setCancelled(true);

                // Apply bounce forward effect
                Vector forward = player.getLocation().getDirection().normalize().multiply(1.5);
                forward.setY(0.4); // Add a bit of upward motion
                player.setVelocity(forward);

                // Decrease shield durability
                shield.setDurability((short) (shield.getDurability() + 1));
                if (shield == mainHandItem) {
                    player.getInventory().setItemInMainHand(shield);
                } else {
                    player.getInventory().setItemInOffHand(shield);
                }

                // Create dynamic particle effects to simulate bouncing
                Location loc = player.getLocation();
                for (int i = 0; i < 360; i += 10) {
                    double angle = i * Math.PI / 180;
                    double radius = 0.5 + random.nextDouble() * 1.0; // Random radius between 0.5 and 1.5
                    double x = loc.getX() + radius * Math.cos(angle);
                    double z = loc.getZ() + radius * Math.sin(angle);
                    double y = loc.getY() + random.nextDouble() * 0.5; // Random height offset between 0 and 0.5
                    player.getWorld().spawnParticle(Particle.CLOUD, x, y, z, 0, 0.0, 0.1, 0.0, 0.1);
                }
                // Play custom sound effect
                player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
            }
        }
    }
}
