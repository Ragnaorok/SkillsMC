package ragnaorok.Main;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import java.util.concurrent.TimeUnit;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import ragnaorok.Main.managers.CurrencyManager;

public class MobListener implements Listener {
    public Main plugin;

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) throws InterruptedException {
        if (event.getEntity() instanceof Monster) {
            Monster monster = (Monster) event.getEntity();
            EntityType type = event.getEntityType();
            Location mloc = monster.getLocation();
            Location particleLoc = mloc.clone();
            if (monster.getKiller() instanceof Player) {
                double phi = Math.PI / 8;
                Player player = monster.getKiller();
                if (type == EntityType.SKELETON || type == EntityType.SKELETON_HORSE || type == EntityType.WITHER_SKELETON
                        || type == EntityType.HUSK || type == EntityType.WITHER) {
                    for (double t = 0; t <= 2 * Math.PI; t += Math.PI / 16) { //Ash Helix
                        for (double i = 0; i <= 1; i += 1) {
                            double x = 0.3 * (2 * Math.PI - t) * 0.5 * cos(t + phi + i * Math.PI);
                            double y = 0.4 * t;
                            double z = 0.3 * (2 * Math.PI - t) * 0.5 * sin(t + phi + i * Math.PI);
                            particleLoc.add(x, y, z);
                            player.spawnParticle(Particle.ASH, particleLoc, 2);
                            particleLoc.subtract(x, y, z);
                        }
                    }
                    for (int i = 0; i < 360; i += 5) { //Ash Circle
                        particleLoc.setY(mloc.getY() + 1);
                        particleLoc.setZ(mloc.getZ() + Math.sin(i));
                        particleLoc.setX(mloc.getX() + Math.cos(i));
                        player.spawnParticle(Particle.WHITE_ASH, particleLoc, 1);
                        player.spawnParticle(Particle.ASH, particleLoc, 1);
                    }
                }
                if (type == EntityType.DROWNED) {
                    for (double t = 0; t <= 2 * Math.PI; t += Math.PI / 16) { //Water Helix
                        for (double i = 0; i <= 1; i += 1) {
                            double x = 0.3 * (2 * Math.PI - t) * 0.5 * cos(t + phi + i * Math.PI);
                            double y = 0.4 * t;
                            double z = 0.3 * (2 * Math.PI - t) * 0.5 * sin(t + phi + i * Math.PI);
                            particleLoc.add(x, y, z);
                            player.spawnParticle(Particle.FALLING_WATER, particleLoc, 2);
                            particleLoc.subtract(x, y, z);
                        }
                    }
                    for (int i = 0; i < 360; i += 5) { //Water Circle
                        particleLoc.setY(mloc.getY());
                        particleLoc.setZ(mloc.getZ() + Math.sin(i));
                        particleLoc.setX(mloc.getX() + Math.cos(i));
                        player.spawnParticle(Particle.WATER_BUBBLE, particleLoc, 1);
                        player.spawnParticle(Particle.WATER_SPLASH, particleLoc, 1);
                    }
                    player.spawnParticle(Particle.SOUL, particleLoc, 10);
                    CurrencyManager manager = new CurrencyManager(plugin);
                    player.spawnParticle(Particle.SOUL, particleLoc, 10);
                    manager.addCurrencyToPlayer(player, +1);
                    player.sendMessage(ChatColor.GREEN + " +1 soul");
                }
                if (type == EntityType.ZOMBIE || type == EntityType.ZOMBIE_HORSE || type == EntityType.ZOMBIE_VILLAGER
                        || type == EntityType.PHANTOM || type == EntityType.ZOMBIFIED_PIGLIN || type == EntityType.PIGLIN) {
                    for (double t = 0; t <= 2 * Math.PI; t += Math.PI / 16) { //Helix
                        for (double i = 0; i <= 1; i += 1) {
                            double x = 0.3 * (2 * Math.PI - t) * 0.5 * cos(t + phi + i * Math.PI);
                            double y = 0.4 * t;
                            double z = 0.3 * (2 * Math.PI - t) * 0.5 * sin(t + phi + i * Math.PI);
                            particleLoc.add(x, y, z);
                            player.spawnParticle(Particle.TOWN_AURA, particleLoc, 1);
                            particleLoc.subtract(x, y, z);
                        }
                    }
                    for (double t = 0; t <= Math.PI; t += Math.PI / 10) { //Sphere
                        double radius = Math.sin(t);
                        double y = Math.cos(t);
                        for (double i = 0; i < Math.PI * 2; i += Math.PI / 10) {
                            double x = Math.cos(i) * radius;
                            double z = Math.sin(i) * radius;
                            mloc.add(x, y, z);
                            player.spawnParticle(Particle.ASH, particleLoc, 1);
                            mloc.subtract(x, y, z);
                        }
                    }
                    CurrencyManager manager = new CurrencyManager(plugin);
                    player.spawnParticle(Particle.SOUL, particleLoc, 10);
                    manager.addCurrencyToPlayer(player, +1);
                    player.sendMessage(ChatColor.GREEN + " +1 soul");
                }

                CurrencyManager manager = new CurrencyManager(plugin);
                if (manager.getPlayerCurrency(player) >= 100) { //Skill:Blood_Lust
                    if (player.getHealth() < 20) {
                        player.setHealth(player.getHealth() + 1);
                        return;
                    }
                }

                if (manager.getPlayerCurrency(player) >= 200) { //Skill:Second_Wind
                    if (player.getHealth() <= 10) {
                        player.setWalkSpeed(player.getWalkSpeed() + 2);
                        TimeUnit.SECONDS.sleep(1);
                        player.setWalkSpeed(player.getWalkSpeed() - 2);
                    }
                }

                if (manager.getPlayerCurrency(player) >= 300) { //Skill:Conqueror
                    if (player.getHealth() >= 15) {
                        player.setInvulnerable(true);
                        TimeUnit.SECONDS.sleep(1);
                        player.setInvulnerable(false);
                    }
                }
            }
        }
    }
}

