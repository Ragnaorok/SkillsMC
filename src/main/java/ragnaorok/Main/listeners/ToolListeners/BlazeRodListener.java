package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Sound.*;
import static org.bukkit.entity.EntityType.FIREBALL;

public class BlazeRodListener implements Listener {
    Map<String, Long> leftCooldown = new HashMap<String, Long>();
    Map<String, Long> shiftCooldown = new HashMap<String, Long>();


    @EventHandler
    public void onBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location origin = player.getLocation();
        Vector direction = origin.getDirection();
        direction.multiply(4);
        Location destination = origin.clone().add(direction).add(direction);
        direction.normalize();
        Material type = player.getItemInHand().getType();
        if (player.getItemInHand() == null) return;
        if (type == Material.BLAZE_ROD) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (!player.isSneaking()) {
                    if (leftCooldown.containsKey(player.getName())) {
                        if (leftCooldown.get(player.getName()) > System.currentTimeMillis()) {
                            long lefttime = (leftCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "Skill will be ready in " + lefttime + " second(s)");
                            return;
                        }
                    }
                }
                if (player.isSneaking()) {
                    if (shiftCooldown.containsKey(player.getName())) {
                        if (shiftCooldown.get(player.getName()) > System.currentTimeMillis()) {
                            long shifttime = (shiftCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "Skill will be ready in " + shifttime + " second(s)");
                            return;
                        }
                    }
                }

                if (!player.isSneaking()) {
                    leftCooldown.put(player.getName(), System.currentTimeMillis() + (3 * 1000));
                    player.sendMessage(ChatColor.GREEN + "Blaze Rod Skill: Fireball");
                    for (int i = 0; i < 3; i++) {  // Fireball Skill
                        Location oloc = destination.add(direction);
                        oloc.getWorld().spawnParticle(Particle.LAVA, oloc, 10);
                        world.spawnEntity(oloc, FIREBALL);
                        world.playSound(oloc, ENTITY_BLAZE_SHOOT, 20, 1);
                        world.playSound(oloc, ENTITY_BLAZE_BURN, 20, 1);
                    }
                }
                if (player.isSneaking()) {
                    shiftCooldown.put(player.getName(), System.currentTimeMillis() + (5 * 1000));
                    destination.add(direction).add(direction);
                    world.createExplosion(destination, 4, false, false);
                    player.sendMessage(ChatColor.GREEN + "Blaze Rod Skill: Implode");
                    for (double t = 0; t <= Math.PI; t += Math.PI / 10) { //Sphere
                        double radius = Math.sin(t);
                        double y = Math.cos(t);
                        for (double i = 0; i < Math.PI * 2; i += Math.PI / 10) {
                            double x = Math.cos(i) * radius;
                            double z = Math.sin(i) * radius;
                            destination.add(x, y, z);
                            world.spawnParticle(Particle.FLASH, destination, 50);
                            destination.subtract(x, y, z);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        EntityType type = event.getEntityType();
        if (type != FIREBALL) return;
        event.setCancelled(true);
    }
}
