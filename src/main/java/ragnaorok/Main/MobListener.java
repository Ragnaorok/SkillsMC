package ragnaorok.Main;

import org.bukkit.*;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import java.util.concurrent.TimeUnit;

public class MobListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) throws InterruptedException {
        if (event.getEntity() instanceof Monster) {
            Monster monster = (Monster) event.getEntity();
            Location mloc = monster.getLocation();
            if (monster.getKiller() instanceof Player) {
                Player player = monster.getKiller();
                Location ploc = player.getLocation();
                player.spawnParticle(Particle.FIREWORKS_SPARK,mloc,51 );
                player.spawnParticle(Particle.SOUL_FIRE_FLAME,mloc,51 );
                if (player.isOp()) {
                    if (player.getHealth() < 20) {
                        player.setHealth(player.getHealth() + 1);
                    } else {
                        return;
                    }
                    //EXTINGUISH effect in a circle
                    for (int i = 0; i < 360; i += 5) {
                        ploc.setZ(ploc.getZ() + Math.cos(i) * 5);
                        ploc.setX(ploc.getX() + Math.sin(i) * 5);
                        ploc.getWorld().playEffect(ploc, Effect.EXTINGUISH, 51);
                    }
                }
                if (player.isOp()) {
                    if (player.getHealth() > 5) {
                        player.setWalkSpeed(player.getWalkSpeed() + 2);
                        TimeUnit.SECONDS.sleep(1);
                        player.setWalkSpeed(player.getWalkSpeed() - 2);
                    }
                }
                if (player.isOp()) {
                    if (player.getHealth() >= 15) {
                        player.setInvulnerable(true);
                        TimeUnit.SECONDS.sleep((long) 0.35);
                        player.setInvulnerable(false);
                    }
                }
            }
        }
    }
}

