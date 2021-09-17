package ragnaorok.Main.listeners.playerDataListeners;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class LevelUpListener implements Listener {
    @EventHandler
    public void onLevelUp(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();
        Location ploc = player.getLocation();
        Location particleLoc = ploc.clone();
        int pre_Level = (player.getLevel() - 1);
        if (player.getLevel() > pre_Level) {
            for (int i = 0; i < 360; i += 5) {
                particleLoc.setY(ploc.getY()+2);
                particleLoc.setZ(ploc.getZ() + Math.sin(i));
                particleLoc.setX(ploc.getX() + Math.cos(i));
                player.spawnParticle(Particle.END_ROD, particleLoc, 1);
            }
        }
    }
}