package ragnaorok.Main.listeners.toolListeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class NetheriteHoeListener implements Listener {
    Map<String, Long> leftCooldown = new HashMap<String, Long>();
    Map<String, Long> shiftCooldown = new HashMap<String, Long>();

    private void groundUpdate(Player player) {  //Cancel glide if player is touching the ground
        Location location = player.getPlayer().getLocation();
        location = location.subtract(0, 1, 0);

        Block block = location.getBlock();

        if (!block.getType().isSolid()) return;

        player.setGliding(false);
    }

    @EventHandler
    public void onCooldown(PlayerMoveEvent event) {
        Player player = event.getPlayer();  //player
        if (leftCooldown.containsKey(player.getName())) {
            if (leftCooldown.get(player.getName()) == System.currentTimeMillis()) {
                player.sendMessage(ChatColor.GREEN + "Phantom Rush is ready");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) { //Calls groundUpdate
        if (!event.getPlayer().isGliding()) return;
        this.groundUpdate(event.getPlayer());
    }

    @EventHandler
    public void onGlideEvent(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.getItemInHand().getType() == Material.NETHERITE_HOE) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) { //Cancels glide if player takes damage while gliding
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.isGliding())
                player.setGliding(false);
        }
    }

    @EventHandler
    public void netheriteHoeEvent(PlayerInteractEvent event) { //Main Function for the Netherite Hoe
        Player player = event.getPlayer();  //player
        Location origin = player.getEyeLocation();  //player eye origin
        Vector direction = origin.getDirection();
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
                            return;
                        }
                    }
                    leftCooldown.put(player.getName(), System.currentTimeMillis() + (6 * 1000));
                    player.sendMessage(ChatColor.GREEN + "Scythe Skill: Phantom Rush");
                    direction.normalize();
                    player.setVelocity(direction);
                    player.setGliding(true);
                }
                if (player.isSneaking()) {
                    if (shiftCooldown.containsKey(player.getName())) {
                        if (shiftCooldown.get(player.getName()) > System.currentTimeMillis()) {
                            long time = (shiftCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "Shulker Bullet will be ready in " + time + " second(s)");
                            return;
                        }
                    }
                    shiftCooldown.put(player.getName(), System.currentTimeMillis() + (8 * 1000));
                    player.sendMessage(ChatColor.GREEN + "Scythe Skill: Shulker Bullet");
                    destination.add(direction).add(direction);
                    Entity entity = world.spawnEntity(new Location(world, destination.getX(), destination.getY() - 1, destination.getZ()), entityType);
                    Vector vec = player.getLocation().getDirection();
                    entity.setVelocity(vec.multiply(4));
                }
            }
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                player.sendMessage(ChatColor.RED + "You cannot use your offhand");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void damageModifier(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Monster || event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (player.getInventory().getItemInMainHand().getType() == Material.NETHERITE_HOE) {
                event.setDamage(event.getDamage() + 8);
            }
        }
    }
}
