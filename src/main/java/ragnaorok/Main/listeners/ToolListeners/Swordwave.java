package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;


public class Swordwave implements Listener { //A Skill that makes Shields usable on mainHand ((;
    Map<String, Long> left_Cooldown = new HashMap<String, Long>();

    @EventHandler
    public void onCast(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();
        Location particleLoc = loc.clone();
        Material type = player.getItemInHand().getType();
        if (player.getItemInHand() == null) return;
        if (type == Material.STICK) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                double yaw = particleLoc.getYaw();
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.SILVER, 1);
                if (left_Cooldown.containsKey(player.getName())) {
                    if (left_Cooldown.get(player.getName()) > System.currentTimeMillis()) {
                        return;
                    }
                }
                left_Cooldown.put(player.getName(), System.currentTimeMillis() + (1 * 1000));
                for (double i = 0; i < 180; i += 10) {
                    double angle = (i + yaw) * Math.PI / 180;
                    double x1 = particleLoc.getX() + 2 * Math.cos(angle);
                    double z1 = particleLoc.getZ() + 2 * Math.sin(angle);
                    double y1 = particleLoc.getY() + Math.random() * Math.sin(angle);
                    player.spawnParticle(Particle.REDSTONE, x1, y1, z1, 5, dustOptions);
                }
            }
        }
    }
}


