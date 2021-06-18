package ragnaorok.Main.listeners.MovementListeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CrouchJumpListener implements Listener {
    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location loc = player.getLocation();
        Location particleLoc = loc.clone();
        Location ploc = loc.clone();
        player.addPotionEffect((new PotionEffect(PotionEffectType.JUMP, 10, 1)));
        for (int i = 0; i < 360; i += 5) { //Magic Circle
            particleLoc.setY(ploc.getY());
            particleLoc.setZ(ploc.getZ() + Math.sin(i));
            particleLoc.setX(ploc.getX() + Math.cos(i));
            world.spawnParticle(Particle.CRIT_MAGIC, particleLoc, 1);
        }
    }
}
