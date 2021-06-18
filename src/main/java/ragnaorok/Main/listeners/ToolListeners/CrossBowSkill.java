package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class CrossBowSkill implements Listener {
    Map<String, Long> leftCooldown = new HashMap<String, Long>();
    Map<String, Long> shiftCooldown = new HashMap<String, Long>();

    @EventHandler
    public void onBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();  //player
        Location origin = player.getLocation();  //player origin
        Vector direction = origin.getDirection();
        Location oloc = origin.add(direction);  // cloning to prevent weird side effects
        Location destination = origin.clone().add(direction).add(direction);  //flashbang destination
        World world = player.getWorld();
        Material type = player.getItemInHand().getType();

        if (player.getItemInHand() == null) return;
        if (type == Material.CROSSBOW) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (!player.isSneaking()) {
                    if (leftCooldown.containsKey(player.getName())) {
                        if (leftCooldown.get(player.getName()) > System.currentTimeMillis()) {
                            long time = (leftCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "Backstep will be ready in " + time + " second(s)");
                            return;
                        }
                    }
                    leftCooldown.put(player.getName(), System.currentTimeMillis() + (3 * 1000));
                    player.sendMessage(ChatColor.GREEN + "Crossbow Skill: Backstep");
                    direction.multiply(-1);
                    direction.normalize();
                    player.setVelocity(direction);
                }
                if (player.isSneaking()) {
                    if (shiftCooldown.containsKey(player.getName())) {
                        if (shiftCooldown.get(player.getName()) > System.currentTimeMillis()) {
                            long time = (shiftCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "Flashbang will be ready in " + time + " second(s)");
                            return;
                        }
                    }
                    shiftCooldown.put(player.getName(), System.currentTimeMillis() + (10 * 1000));
                    player.sendMessage(ChatColor.GREEN + "Crossbow Skill: Flashbang");
                    destination.add(direction).add(direction);
                    oloc.getWorld().spawnParticle(Particle.FLASH, destination, 10);
                    for (double t = 0; t <= Math.PI; t += Math.PI / 10) { //Sphere
                        double radius = Math.sin(t);
                        double y = Math.cos(t);
                        for (double i = 0; i < Math.PI * 2; i += Math.PI / 10) {
                            double x = Math.cos(i) * radius;
                            double z = Math.sin(i) * radius;
                            destination.add(x, y, z);
                            oloc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, destination, 10);
                            destination.subtract(x, y, z);
                        }
                    }
                    world.createExplosion(destination, 1, false, false);
                }
            }
        }
    }
}