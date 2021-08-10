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
import ragnaorok.Main.ClassType;
import ragnaorok.Main.managers.PlayerClassManager;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Sound.*;
import static org.bukkit.entity.EntityType.FIREBALL;

public class BlazeRodListener implements Listener {
    Map<String, Long> leftCooldown = new HashMap<String, Long>();
    Map<String, Long> shiftCooldown = new HashMap<String, Long>();


    @EventHandler
    public void onCast(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            PlayerClassManager.useAbility(player);
            PlayerClassManager.displayManaBar(player);
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            PlayerClassManager.addMana();
            PlayerClassManager.displayManaBar(player);
        }
        }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        EntityType type = event.getEntityType();
        if (type != FIREBALL) return;
        event.setCancelled(true);
    }
}
