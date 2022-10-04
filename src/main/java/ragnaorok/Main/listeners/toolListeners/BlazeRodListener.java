package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import static org.bukkit.entity.EntityType.FIREBALL;
import static ragnaorok.Main.managers.PlayerClassSkills.*;

public class BlazeRodListener implements Listener {

    @EventHandler
    public void onCast(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            useLeftSkill(player);
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            useRightSkill(player);
        }

    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        EntityType type = event.getEntityType();
        Vector v = entity.getVelocity().setX(0).setY(0).setZ(0);
        World world = event.getLocation().getWorld();
        if (type != FIREBALL) return;
        Location loc = event.getLocation();
        world.createExplosion(loc, 1, false, false);
        event.setCancelled(true);
        entity.setVelocity(v);
    }
}
