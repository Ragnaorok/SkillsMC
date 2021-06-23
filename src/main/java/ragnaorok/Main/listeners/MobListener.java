package ragnaorok.Main.listeners;

import com.sun.xml.internal.bind.v2.TODO;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import ragnaorok.Main.MobDeathParticles.Shapes;
import ragnaorok.Main.managers.SoulsManager;

public class MobListener implements Listener {
//TODO: Further revision/refactoring required

    public static void draw(Shapes shape, Location particleLoc, Location mloc, Particle particle, Player player) {
        switch (shape) {
            case CIRCLE:
                for (int i = 0; i < 360; i += 5) {
                    particleLoc.setY(mloc.getY() + 1);
                    particleLoc.setZ(mloc.getZ() + Math.sin(i));
                    particleLoc.setX(mloc.getX() + Math.cos(i));
                    player.spawnParticle(particle, particleLoc, 1);
                    player.spawnParticle(particle, particleLoc, 1);
                }
            case HELIX:
                double phi = Math.PI / 8;
                for (double t = 0; t <= 2 * Math.PI; t += Math.PI / 16) {
                    for (double i = 0; i <= 1; i += 1) {
                        double x = 0.3 * (2 * Math.PI - t) * 0.5 * cos(t + phi + i * Math.PI);
                        double y = 0.4 * t;
                        double z = 0.3 * (2 * Math.PI - t) * 0.5 * sin(t + phi + i * Math.PI);
                        particleLoc.add(x, y, z);
                        player.spawnParticle(particle, particleLoc, 2);
                        particleLoc.subtract(x, y, z);
                    }
                }
            case SPHERE:
                for (double t = 0; t <= Math.PI; t += Math.PI / 10) {
                    double radius = Math.sin(t);
                    double y = Math.cos(t);
                    for (double i = 0; i < Math.PI * 2; i += Math.PI / 10) {
                        double x = Math.cos(i) * radius;
                        double z = Math.sin(i) * radius;
                        mloc.add(x, y, z);
                        player.spawnParticle(particle, particleLoc, 1);
                        mloc.subtract(x, y, z);
                    }
                }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster) {
            Monster monster = (Monster) event.getEntity();
            EntityType type = event.getEntityType();
            Location mloc = monster.getLocation();
            Location particleLoc = mloc.clone();

            if (monster.getKiller() instanceof Player) {
                Player player = monster.getKiller();

                if (type == EntityType.SKELETON || type == EntityType.SKELETON_HORSE || type == EntityType.WITHER_SKELETON
                        || type == EntityType.HUSK || type == EntityType.WITHER) {
                    draw(Shapes.HELIX,particleLoc,mloc, Particle.ASH, player);
                    draw(Shapes.CIRCLE,particleLoc,mloc, Particle.WHITE_ASH, player);
                    draw(Shapes.CIRCLE,particleLoc,mloc, Particle.ASH, player);
                }

                if (type == EntityType.DROWNED) {
                    draw(Shapes.HELIX,particleLoc,mloc, Particle.FALLING_WATER, player);
                    draw(Shapes.CIRCLE,particleLoc,mloc, Particle.WATER_BUBBLE, player);
                    draw(Shapes.CIRCLE,particleLoc,mloc, Particle.WATER_SPLASH, player);
                    player.spawnParticle(Particle.SOUL, particleLoc, 10);
                    SoulsManager.addCurrencyToPlayer(player, 1);
                    player.sendMessage(ChatColor.GREEN + " +1 soul");
                }

                if (type == EntityType.ZOMBIE || type == EntityType.ZOMBIE_HORSE || type == EntityType.ZOMBIE_VILLAGER || type == EntityType.PILLAGER
                        || type == EntityType.PHANTOM || type == EntityType.ZOMBIFIED_PIGLIN || type == EntityType.PIGLIN) {
                    draw(Shapes.HELIX,particleLoc,mloc, Particle.TOWN_AURA, player);
                    draw(Shapes.SPHERE,particleLoc,mloc, Particle.ASH, player);
                    player.spawnParticle(Particle.SOUL, particleLoc, 10);
                    SoulsManager.addCurrencyToPlayer(player, +1);
                    player.sendMessage(ChatColor.GREEN + " +1 soul");
                }

                if (type == EntityType.ENDERMAN) {
                    for (int j = 0; j < 4; j++) {
                        for (int i = 0; i < 360; i += 5) { //Obsidian Tear Circle
                            particleLoc.setZ(mloc.getZ() + Math.sin(i));
                            particleLoc.setX(mloc.getX() + Math.cos(i));
                            player.spawnParticle(Particle.FALLING_OBSIDIAN_TEAR, particleLoc, 1);
                            particleLoc.setY(mloc.getY()+1);
                        }
                        draw(Shapes.HELIX,particleLoc,mloc, Particle.PORTAL, player);
                    }

// Skills below this belong in a different class, further revision and refactoring is required
                    if (SoulsManager.getPlayerCurrency(player) > 99) { //Skill:Blood_Lust
                        if (player.getHealth() < 20) {
                            player.setHealth(player.getHealth() + 1);
                        }
                    }
                    if (SoulsManager.getPlayerCurrency(player) > 199) { //Skill:Second_Wind
                        if (player.getHealth() <= 10) {
                            player.addPotionEffect((new PotionEffect(PotionEffectType.SPEED, 15, 1)));
                        }
                    }
                    if (SoulsManager.getPlayerCurrency(player) > 299) { //Skill:Conqueror
                        if (player.getHealth() >= 15) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 15, 1));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 15, 1));
                        }
                    }
                }
            }
        }
    }
}

