package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class NetheriteHoeListener implements Listener {
    Map<String, Long> leftCooldown = new HashMap<String, Long>();
    Map<String, Long> shiftCooldown = new HashMap<String, Long>();

    @EventHandler
    public void onBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();  //player
        Location origin = player.getEyeLocation();  //player origin
        Vector direction = origin.getDirection();
        Location oloc = origin.add(direction);  //cloning to prevent weird side effects
        Location destination = origin.clone().add(direction).add(direction); //destination
        World world = player.getWorld();
        EntityType entityType = EntityType.SHULKER_BULLET;
        Material type = player.getItemInHand().getType();
        if (player.getItemInHand() == null) return;
        if (type == Material.NETHERITE_HOE) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (!player.isSneaking()) {
                    if (leftCooldown.containsKey(player.getName())) {
                        if (leftCooldown.get(player.getName()) > System.currentTimeMillis()) {
                            long time = (leftCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "Reap will be ready in " + time + " second(s)");
                            return;
                        }
                    }
                    leftCooldown.put(player.getName(), System.currentTimeMillis() + (3 * 1000));
                    player.sendMessage(ChatColor.GREEN + "Scythe Skill: Phantom Rush");
                    direction.normalize();
                    direction.multiply(2);
                    player.setVelocity(direction);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30, 3));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15, 3));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 50, 3));
                }
                if (player.isSneaking()) {
                    if (shiftCooldown.containsKey(player.getName())) {
                        if (shiftCooldown.get(player.getName()) > System.currentTimeMillis()) {
                            long time = (shiftCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "Cursed Bullet will be ready in " + time + " second(s)");
                            return;
                        }
                    }
                    shiftCooldown.put(player.getName(), System.currentTimeMillis() + (6 * 1000));
                    player.sendMessage(ChatColor.GREEN + "Scythe Skill: Cursed Bullet");
                    destination.add(direction).add(direction);
                    Entity entity = world.spawnEntity(new Location(world, destination.getX(), destination.getY()-1, destination.getZ()), entityType);
                    oloc.getWorld().spawnParticle(Particle.PORTAL, oloc, 10);
                    direction.add(direction);
                    entity.setVelocity(direction);
                }
            }
        }
    }
}
