package ragnaorok.Main.listeners.movementListeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class DoubleJumpListener implements Listener {

    private void groundUpdate(Player player) {
        Location location = player.getPlayer().getLocation();
        location = location.subtract(0, 1, 0);

        Block block = location.getBlock();
        if (!block.getType().isSolid()) return;

        player.setAllowFlight(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {

        this.groundUpdate(event.getPlayer());
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getAllowFlight()) return;
        this.groundUpdate(event.getPlayer());
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR)
            return;

        event.setCancelled(true);
        event.getPlayer().setAllowFlight(false);
        event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(1.2).setY(1.0));
    }
}