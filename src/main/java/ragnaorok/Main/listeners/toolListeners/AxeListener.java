package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import ragnaorok.Main.Tools;

import java.util.HashMap;
import java.util.Map;

public class AxeListener implements Listener {
    Map<String, Long> axeCooldown = new HashMap<>();

    @EventHandler
    public void onAxeHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (Tools.getMainHand(player.getInventory()) == Tools.AXE) {
                if (event.getEntity() instanceof Monster) {
                    if (!player.isSprinting()) {
                        player.sendMessage(ChatColor.DARK_GRAY + "You must be sprinting to use Shockwave.");
                        return;
                    }
                    if (axeCooldown.containsKey(player.getName())) {
                        if (axeCooldown.get(player.getName()) > System.currentTimeMillis()) {
                            long time = (axeCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "Shockwave will be ready in " + time + " second(s)");
                            return;
                        }
                    }
                    axeCooldown.put(player.getName(), System.currentTimeMillis() + (1 * 1000)); // 1 second cooldown for testing
                    player.sendMessage(ChatColor.GREEN + "Axe Skill: Shockwave");

                    // Create shockwave effect originating from the mob's location
                    createShockwave(event.getEntity().getLocation(), player);
                }
            }
        }
    }

    private void createShockwave(Location origin, Player player) {
        World world = origin.getWorld();
        Vector direction = player.getLocation().getDirection().normalize();
        double playerVelocityMagnitude = player.getVelocity().length();
        double baseDamage = 9.0;
        double damage = baseDamage * playerVelocityMagnitude; // Damage scales with player velocity

        for (int i = 1; i <= 10; i++) { // Shockwave length
            Location shockwaveLoc = origin.clone().add(direction.multiply(i));
            world.spawnParticle(Particle.EXPLOSION_LARGE, shockwaveLoc, 1, 0.2, 0.2, 0.2, 0);
            world.spawnParticle(Particle.BLOCK_CRACK, shockwaveLoc, 10, 0.5, 0.5, 0.5, Material.STONE.createBlockData());
            world.spawnParticle(Particle.CLOUD, shockwaveLoc, 5, 0.2, 0.2, 0.2, 0);
            world.playSound(shockwaveLoc, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f); // Play metal impact sound
            world.playSound(shockwaveLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.1f); // Play explosion sound

            for (Entity entity : world.getNearbyEntities(shockwaveLoc, 1, 1, 1)) {
                if (entity instanceof LivingEntity && entity != player) {
                    ((LivingEntity) entity).damage(damage); // Deal velocity-scaled damage to entities in the path
                    Vector knockback = direction.clone().multiply(2); // Adjust knockback strength as needed
                    knockback.setY(0.5); // Add some vertical knockback
                    entity.setVelocity(knockback);
                }
            }
        }
        player.sendMessage("You dealt: " + damage);
    }

    @EventHandler
    public void reinforceEnchant(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Monster) {
            Player player = (Player) event.getDamager();
            if (player.getInventory().getItemInMainHand().getType() == Material.DIAMOND_AXE || player.getInventory().getItemInMainHand().getType() == Material.IRON_AXE || player.getInventory().getItemInMainHand().getType() == Material.STONE_AXE || player.getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE || player.getInventory().getItemInMainHand().getType() == Material.WOODEN_AXE) {
                if (player.getInventory().getItemInMainHand().getItemMeta().getLore().contains("Reinforce"))
                    event.setDamage(event.getDamage() + 5);
            }
        }
    }
}
