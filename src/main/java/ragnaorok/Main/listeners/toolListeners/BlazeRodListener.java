package ragnaorok.Main.listeners.toolListeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import ragnaorok.Main.managers.ManaManager;
import ragnaorok.Main.managers.PlayerClassManager;

import static org.bukkit.entity.EntityType.FIREBALL;

public class BlazeRodListener implements Listener {

    @EventHandler
    public void onCast(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            PlayerClassManager.useAbility(player);
            PlayerClassManager.displayManaBar(player);
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (ManaManager.getMana(player) < 10) {
                ManaManager.addMana(player, 1);
            } else {
                player.sendMessage("You have full mana");
            }
            PlayerClassManager.displayManaBar(player);
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
