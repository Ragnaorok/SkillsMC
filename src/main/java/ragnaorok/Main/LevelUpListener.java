package ragnaorok.Main;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class LevelUpListener implements Listener {
    @EventHandler
    public void onLevelUp(PlayerLevelChangeEvent event) {
        Player player = event.getPlayer();
        Location ploc = player.getLocation();
        Location particleLoc = ploc.clone();
        int pre_Level = (player.getLevel() - 1);
        if (player.getLevel() > pre_Level) {
            player.spawnParticle(Particle.FIREWORKS_SPARK, ploc, 20);
            for (int i = 0; i < 360; i += 5) {
                particleLoc.setY(ploc.getY()+1);
                particleLoc.setZ(ploc.getZ() + Math.sin(i) * 3);
                particleLoc.setX(ploc.getX() + Math.cos(i) * 3);
                player.spawnParticle(Particle.ENCHANTMENT_TABLE, particleLoc, 50);
            }
        }
    }
}