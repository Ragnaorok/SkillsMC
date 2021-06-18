package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class CrossBowSkill implements Listener {
    Map<String, Long> cooldown = new HashMap<String, Long>();

    @EventHandler
    public void onBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location loc = player.getLocation();
        Location particleLoc = loc.clone();
        Location ploc = loc.clone();
        Material type = player.getItemInHand().getType();
        if (player.getItemInHand() == null) return;
        if (type == Material.CROSSBOW) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (cooldown.containsKey(player.getName())) {
                    if (cooldown.get(player.getName()) > System.currentTimeMillis()) {
                        long time = (cooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                        player.sendMessage(ChatColor.DARK_GRAY + "PowerShot will be ready in " + time + " second(s)");
                        return;
                    }
                }
                cooldown.put(player.getName(), System.currentTimeMillis() + (5 * 1000));
                player.sendMessage(ChatColor.GREEN + "Offensive Skill: PowerShot");
                Location origin = player.getLocation(); // Spawns a particle
                Vector direction = origin.getDirection();
                direction.multiply(10);
                Location destination = origin.clone().add(direction);
                direction.normalize();
                for (int i = 0; i < 10; i++) {
                    Location oloc = origin.add(direction);
                    oloc.getWorld().spawnParticle(Particle.FLASH, oloc, 10);
                    player.getWorld().spawnEntity(oloc, EntityType.ARROW);
                }
            }
        }
    }
}