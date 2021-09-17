package ragnaorok.Main.listeners.toolListeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;


import java.util.HashMap;
import java.util.Map;

public class Test implements Listener {
    Map<String, Long> leftCooldown = new HashMap<String, Long>();
    Map<String, Long> shiftCooldown = new HashMap<String, Long>();
    Map<String, Long> duration = new HashMap<String, Long>();

    @EventHandler
    public void lightningRod(PlayerInteractEvent event) {
        EntityType entityType = EntityType.LIGHTNING;
        Player player = event.getPlayer();  //player
        Location origin = player.getEyeLocation();  //player eye origin
        Vector direction = origin.getDirection();
        Location destination = origin.clone().add(direction).add(direction); //destination
        World world = player.getWorld();
        Material type = player.getItemInHand().getType();
        if (player.getItemInHand() == null) return;
        if (type == Material.LIGHTNING_ROD) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (player.isSneaking()) {
                    if (shiftCooldown.containsKey(player.getName())) {
                        if (shiftCooldown.get(player.getName()) > System.currentTimeMillis()) {
                            long time = (shiftCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "" + time + " second(s)");
                            return;
                        }
                    }
                    shiftCooldown.put(player.getName(), System.currentTimeMillis() + (3 * 1000));
                    player.sendMessage(ChatColor.GREEN + " Thunderclap");
                }
            }
        }
    }
    @EventHandler
    public void Blight(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Monster) {
            Player player = (Player) event.getDamager();
            Material type = player.getItemInHand().getType();
            if (type  == Material.LIGHTNING_ROD) {
                World world = player.getWorld();
                Monster monster = (Monster) event.getEntity();
                Location loc = monster.getLocation();
                Location mloc = loc.clone();
                if (duration.get(player.getName()) == null)
                    return;
                if (duration.get(player.getName()) > System.currentTimeMillis()) {
                    EntityType Fang = EntityType.FALLING_BLOCK;
                    world.strikeLightning(mloc);
                    player.sendMessage("Blight");
                }
            }
        }
    }
}



