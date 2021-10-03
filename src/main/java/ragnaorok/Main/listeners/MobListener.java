package ragnaorok.Main.listeners;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import static ragnaorok.Main.MobDeathParticles.ParticleShapes.draw;

import ragnaorok.Main.MobDeathParticles.EnumShapes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MobListener implements Listener {
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
        draw(EnumShapes.HELIX, mloc, Particle.TOWN_AURA, player);
        draw(EnumShapes.SPHERE, mloc, Particle.ASH, player);
        player.spawnParticle(Particle.SOUL, mloc, 10);
        SoulsManager.addSoulsToPlayer(player, +1);
        player.sendMessage(ChatColor.GREEN + " +1 soul");
    }

    @MobHandler(entity = EntityType.SKELETON, entities = {EntityType.SKELETON_HORSE,
            EntityType.WITHER_SKELETON, EntityType.HUSK, EntityType.WITHER})
    public static void handleSkeletonsDeath(EntityDeathEvent event, Player player, Monster monster) {
        Location mloc = monster.getLocation();
        draw(EnumShapes.HELIX, mloc, Particle.ASH, player);
        draw(EnumShapes.CIRCLE, mloc, Particle.WHITE_ASH, player);
        draw(EnumShapes.CIRCLE, mloc, Particle.ASH, player);
    }

    @MobHandler(entity = EntityType.DROWNED)
    public static void handleDrownedDeath(EntityDeathEvent event, Player player, Monster monster) {
        Location mloc = monster.getLocation();
        draw(EnumShapes.HELIX, mloc, Particle.FALLING_WATER, player);
        draw(EnumShapes.CIRCLE, mloc, Particle.WATER_BUBBLE, player);
        draw(EnumShapes.CIRCLE, mloc, Particle.WATER_SPLASH, player);
        player.spawnParticle(Particle.SOUL, mloc, 10);
        SoulsManager.addSoulsToPlayer(player, 1);
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
            draw(EnumShapes.HELIX, mloc, Particle.PORTAL, player);
        }
    }

    @MobHandler(entity = EntityType.CREEPER)
    public static void handleCreeperDeath(EntityDeathEvent event, Player player, Monster monster) {
        Location mloc = monster.getLocation();
        Location particleLoc = mloc.clone();
        draw(EnumShapes.HELIX, particleLoc, Particle.FIREWORKS_SPARK, player);
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
            if (monster.getKiller() instanceof Player) {
                Player player = monster.getKiller();
                Method method = handlers.get(monster.getType());
                if (method != null) {
                    method.invoke(this, event, player, monster);
                }
            }
        }
    }
}

