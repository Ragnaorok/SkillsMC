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
                right_Cooldown.put(player.getName(), System.currentTimeMillis() + (5 * 1000));
                player.sendMessage(ChatColor.GREEN + "Shield Skill: Brace");
                player.addPotionEffect((brace));
                for (int i = 0; i < 360; i += 5) { //Magic Circle
                    particleLoc.setY(ploc.getY() + 1);
                    particleLoc.setZ(ploc.getZ() + Math.sin(i));
                    particleLoc.setX(ploc.getX() + Math.cos(i));
                    world.spawnParticle(Particle.SPELL_INSTANT, particleLoc, 1);
                }
            } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) { //Unfinished Stub
                if (player.isSneaking()) {
                    {
                        if (left_Cooldown.containsKey(player.getName())) {

                            if (left_Cooldown.get(player.getName()) > System.currentTimeMillis()) {
                                return;
                            }
                        }
                    }
                    left_Cooldown.put(player.getName(), System.currentTimeMillis() + (3 * 1000));
                    player.sendMessage(ChatColor.GREEN + "Shield Skill: Vorpal Spikes");
                    Location origin = player.getLocation();
                    Vector direction = origin.getDirection();
                    direction.multiply(10);
                    direction.normalize();
                    for (int i = 0; i < 10; i++) {
                        Location oloc = origin.add(direction);
                        oloc.getWorld().spawnParticle(Particle.CRIT, oloc, 10);
                        player.getWorld().spawnEntity(oloc, Fang);
                    }
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

