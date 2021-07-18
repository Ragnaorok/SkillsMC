package ragnaorok.Main.listeners;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import ragnaorok.Main.MobDeathParticles.Shapes;
import ragnaorok.Main.managers.SoulsManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MobListener implements Listener {

    static ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
    private static final Map<EntityType, Method> handlers = new HashMap<>();

    public MobListener() {
        initHandlers();
    }

    // @author Brandon
    // This section are the handlers for all mob events and are using annotations for neatness and expandability
    // @MobHandler() requires entity field to be filled out at all times

    @MobHandler(entity = EntityType.ZOMBIE, entities = {EntityType.ZOMBIE_HORSE,
            EntityType.ZOMBIE_VILLAGER, EntityType.PILLAGER, EntityType.PHANTOM,
            EntityType.ZOMBIFIED_PIGLIN, EntityType.PIGLIN, EntityType.WITCH})
    public static void handleZombiesDeath(EntityDeathEvent event, Player player, Monster monster) {
        Location mloc = monster.getLocation();
        draw(Shapes.HELIX, mloc, Particle.TOWN_AURA, player);
        draw(Shapes.SPHERE, mloc, Particle.ASH, player);
        player.spawnParticle(Particle.SOUL, mloc, 10);
        SoulsManager.addCurrencyToPlayer(player, +1);
        player.sendMessage(ChatColor.GREEN + " +1 soul");
    }

    @MobHandler(entity = EntityType.SKELETON, entities = {EntityType.SKELETON_HORSE,
            EntityType.WITHER_SKELETON, EntityType.HUSK, EntityType.WITHER})
    public static void handleSkeletonsDeath(EntityDeathEvent event, Player player, Monster monster) {
        Location mloc = monster.getLocation();
        draw(Shapes.HELIX, mloc, Particle.ASH, player);
        draw(Shapes.CIRCLE, mloc, Particle.WHITE_ASH, player);
        draw(Shapes.CIRCLE, mloc, Particle.ASH, player);
    }

    @MobHandler(entity = EntityType.DROWNED)
    public static void handleDrownedDeath(EntityDeathEvent event, Player player, Monster monster) {
        Location mloc = monster.getLocation();
        draw(Shapes.HELIX, mloc, Particle.FALLING_WATER, player);
        draw(Shapes.CIRCLE, mloc, Particle.WATER_BUBBLE, player);
        draw(Shapes.CIRCLE, mloc, Particle.WATER_SPLASH, player);
        player.spawnParticle(Particle.SOUL, mloc, 10);
        SoulsManager.addCurrencyToPlayer(player, 1);
        player.sendMessage(ChatColor.GREEN + " +1 soul");
    }

    @MobHandler(entity = EntityType.ENDERMAN)
    public static void handleEnderManDeath(EntityDeathEvent event, Player player, Monster monster) {
        Location mloc = monster.getLocation();
        Location particleLoc = mloc.clone();
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 360; i += 5) { //Obsidian Tear Circle
                particleLoc.setZ(mloc.getZ() + Math.sin(i));
                particleLoc.setX(mloc.getX() + Math.cos(i));
                particleLoc.setY(mloc.getY() + 1);
                player.spawnParticle(Particle.FALLING_OBSIDIAN_TEAR, particleLoc, 1);
            }
            draw(Shapes.HELIX, mloc, Particle.PORTAL, player);
        }
    }

    @MobHandler(entity = EntityType.CREEPER)
    public static void handleCreeperDeath(EntityDeathEvent event, Player player, Monster monster) {
        Location mloc = monster.getLocation();
        Location particleLoc = mloc.clone();
        draw(Shapes.HELIX, particleLoc, Particle.FIREWORKS_SPARK, player);
        int chance = (int) Math.random()*10;
        if (chance == 10){
            player.getInventory().addItem(firework);
        }
    }


    public void initHandlers() {
        for (Method method : this.getClass().getMethods()) {
            MobHandler mobHandler = method.getAnnotation(MobHandler.class);
            if (mobHandler != null) {
                handlers.put(mobHandler.entity(), method);
                for (EntityType type : mobHandler.entities()) {
                    handlers.put(type, method);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) throws InvocationTargetException, IllegalAccessException {
        if (event.getEntity() instanceof Monster) {
            Monster monster = (Monster) event.getEntity();
            EntityType type = event.getEntityType();
            Location mloc = monster.getLocation();
            Location particleLoc = mloc.clone();

            if (monster.getKiller() instanceof Player) {
                Player player = monster.getKiller();
                Method method = handlers.get(monster.getType());
                if (method != null) {
                    method.invoke(this, event, player, monster);
                }
                // Skills below this belong in a different class, further revision and refactoring is required
                if (SoulsManager.getPlayerCurrency(player) > 99) { //Skill:Blood_Lust
                    if (player.getHealth() < 20) {
                        player.setHealth(player.getHealth() + 1);
                    }
                    else{
                        return;
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

    // TODO: I would move this function else where - Brandon
    public static void draw(Shapes shape, Location mloc, Particle particle, Player player) {
        Location particleLoc = mloc.clone();
        switch (shape) {
            case CIRCLE:
                for (int i = 0; i < 360; i += 5) {
                    particleLoc.setY(mloc.getY());
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
}

