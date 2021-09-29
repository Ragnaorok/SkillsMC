package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.entity.EntityType.FIREBALL;
import static ragnaorok.Main.managers.PlayerClassSkills.*;

public class BlazeRodListener implements Listener {
    Map<String, Long> leftCooldown = new HashMap<>();

    @EventHandler
    public void onCast(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (leftCooldown.containsKey(player.getName())) {
                if (leftCooldown.get(player.getName()) > System.currentTimeMillis()) {
                    long time = (leftCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                    player.sendMessage(ChatColor.DARK_GRAY + "Fireball will be ready in " + time + " second(s)");
                    return;
                }
            }
            leftCooldown.put(player.getName(), System.currentTimeMillis() + (4 * 1000));
            useLeftSkill(player);
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            useRightSkill(player);
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        EntityType type = event.getEntityType();
        World world = event.getLocation().getWorld();
        if (type != FIREBALL) return;
        Location loc = event.getLocation();
        world.createExplosion(loc, 1, false, false);
        event.setCancelled(true);
    }
}
