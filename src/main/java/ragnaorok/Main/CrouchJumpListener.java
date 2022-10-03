package ragnaorok.Main;

import org.bukkit.Location;
import org.bukkit.Particle;
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
        Location loc = player.getLocation();
        Location particleLoc = loc.clone();
        player.addPotionEffect((new PotionEffect(PotionEffectType.JUMP, 10, 1)));
        player.spawnParticle(Particle.SONIC_BOOM, particleLoc, 1);
    }
}
