package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;


public class ShieldListener implements Listener { //A Skill that makes Shields usable on mainHand ((;
    Map<String, Long> left_Cooldown = new HashMap<String, Long>();
    Map<String, Long> right_Cooldown = new HashMap<String, Long>();
    EntityType Fang = EntityType.EVOKER_FANGS;

    @EventHandler
    public void onBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location loc = player.getLocation();
        Location particleLoc = loc.clone();
        Location ploc = loc.clone();
        Material type = player.getItemInHand().getType();
        double yaw = particleLoc.getYaw();
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.SILVER, 1);
        PotionEffect brace = new PotionEffect(PotionEffectType.ABSORPTION, 25, 1);
        if (player.getItemInHand() == null) return;
        if (type == Material.SHIELD) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (right_Cooldown.containsKey(player.getName())) {
                    if (right_Cooldown.get(player.getName()) > System.currentTimeMillis()) {
                        long time = (right_Cooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                        player.sendMessage(ChatColor.DARK_GRAY + "Brace will be ready in " + time + " second(s)");
                        return;
                    }
                }
                right_Cooldown.put(player.getName(), System.currentTimeMillis() + (2 * 1000));
                player.sendMessage(ChatColor.GREEN + "Shield Skill: Brace");
                player.addPotionEffect((brace));
                for (double i = 0; i < 180; i += 10) {
                    double angle = (i + yaw) * Math.PI / 180;
                    double x1 = particleLoc.getX() + 2 * Math.cos(angle);
                    double z1 = particleLoc.getZ() + 2 * Math.sin(angle);
                    double y1 = particleLoc.getY() + 0.4 * Math.sin(angle);
                    player.spawnParticle(Particle.REDSTONE, x1, y1, z1 ,5, dustOptions);
                }
            } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    double y1 = particleLoc.getY() + 0.6;
                    for (double i = 0; i < 120; i += 10) {
                        double angle = (i + yaw) * Math.PI / 180;
                        double x1 = particleLoc.getX() + 1.5 * Math.cos(angle);
                        double z1 = particleLoc.getZ() + 1.5 * Math.sin(angle);
                        player.spawnParticle(Particle.REDSTONE, x1, y1, z1 ,5, dustOptions);
                        y1  = y1 + (Math.random()*0.5) * Math.sin(angle);
                    }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof EvokerFangs) {
            if (event.getEntityType() != EntityType.PLAYER) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void reinforceEnchant(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Monster) {
            Player player = (Player) event.getDamager();
            if (player.getInventory().getItemInMainHand().getType() == Material.SHIELD) {
                if (player.getInventory().getItemInMainHand().getItemMeta().getLore().contains("Reinforce"))
                    event.setDamage(event.getDamage() + 5);
            }
        }
    }
}

