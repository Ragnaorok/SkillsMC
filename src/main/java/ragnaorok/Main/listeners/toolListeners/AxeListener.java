package ragnaorok.Main.listeners.ToolListeners;
import ragnaorok.Main.skills.SkillAccess;
import ragnaorok.Main.progression.PlayerStats;
import static ragnaorok.Main.skills.SkillAccess.WeaponSkill.AXE;


import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import ragnaorok.Main.Loadout;

import java.util.HashMap;
import java.util.Map;

public class AxeListener implements Listener {
    Map<java.util.UUID, Long> axeCooldown = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onAxeHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();

            if (Loadout.getMainHand(player.getInventory()) == Loadout.AXE) {
                if (event.getEntity() instanceof Monster) {
                    if (!SkillAccess.require(player, AXE)) return;
                    if (!player.isSprinting()) {
                        player.sendMessage(ChatColor.DARK_GRAY + "You must be sprinting to use Shockwave.");
                        return;
                    }
                    if (axeCooldown.containsKey(player.getUniqueId())) {
                        if (axeCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                            long time = (axeCooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "Shockwave will be ready in " + time + " second(s)");
                            return;
                        }
                    }
                    axeCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (3 * 1000)); // 1 second cooldown for testing
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
        double damage = PlayerStats.damage(player, baseDamage * playerVelocityMagnitude); // Damage scales with player velocity

        for (int i = 1; i <= 10; i++) { // Shockwave length
            Location shockwaveLoc = origin.clone().add(direction.multiply(i));
            world.spawnParticle(Particle.EXPLOSION, shockwaveLoc, 1, 0.2, 0.2, 0.2, 0);
            world.spawnParticle(Particle.BLOCK, shockwaveLoc, 10, 0.5, 0.5, 0.5, Material.STONE.createBlockData());
            world.spawnParticle(Particle.CLOUD, shockwaveLoc, 5, 0.2, 0.2, 0.2, 0);
            world.playSound(shockwaveLoc, Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f); // Play metal impact sound
            world.playSound(shockwaveLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.1f); // Play explosion sound

            for (Entity entity : world.getNearbyEntities(shockwaveLoc, 1, 1, 1)) {
                if (entity instanceof LivingEntity && entity != player) {
                    ((LivingEntity) entity).damage(damage, player); // Deal velocity-scaled damage to entities in the path
                    Vector knockback = direction.clone().multiply(3); // knockback strength
                    knockback.setY(0.5); // Add some vertical knockback
                    entity.setVelocity(knockback);
                }
            }
        }
        player.sendMessage("You dealt: " + damage);
    }
}
