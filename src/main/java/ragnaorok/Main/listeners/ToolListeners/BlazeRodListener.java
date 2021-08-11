package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import ragnaorok.Main.managers.ManaManager;
import ragnaorok.Main.managers.PlayerClassManager;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.entity.EntityType.FIREBALL;

public class BlazeRodListener implements Listener {

    @EventHandler
    public void onCast(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            PlayerClassManager.useAbility(player);
            PlayerClassManager.displayManaBar(player);
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (ManaManager)
            ManaManager.addMana(player, 1);
            PlayerClassManager.displayManaBar(player);
        }
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        EntityType type = event.getEntityType();
        World world = event.getLocation().getWorld();
        if (type != FIREBALL) return;
        Location loc = event.getLocation();
        world.createExplosion(loc,1 , false, false);
        event.setCancelled(true);
    }
}
