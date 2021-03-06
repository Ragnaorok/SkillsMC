package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import ragnaorok.Main.ClassType;
import ragnaorok.Main.Constant;
import ragnaorok.Main.SkillsMCPlayer;

import java.util.HashMap;
import java.util.Map;

public class BowListener implements Listener {
    Map<String, Long> leftCooldown = new HashMap<>();
    Map<String, Long> shiftCooldown = new HashMap<>();
    Map<String, Long> duration = new HashMap<>();

    @EventHandler
    public void bowListener(PlayerInteractEvent event) {
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 25, 3);
        Player player = event.getPlayer();  //player
        Material type = player.getItemInHand().getType();
        if (player.getItemInHand() == null) return;
        if (type == Material.BOW) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (!player.isSneaking()) {
                    if (leftCooldown.containsKey(player.getName())) {
                        if (leftCooldown.get(player.getName()) > System.currentTimeMillis()) {
                            long time = (leftCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "Speed will be ready in " + time + " second(s)");
                            return;
                        }
                    }
                    leftCooldown.put(player.getName(), System.currentTimeMillis() + (10 * 1000));
                    player.addPotionEffect(speed);
                }

                if (player.isSneaking()) {
                    if (shiftCooldown.containsKey(player.getName())) {
                        if (shiftCooldown.get(player.getName()) > System.currentTimeMillis()) {
                            long time = (shiftCooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "" + time + " second(s)");
                            return;
                        }
                    }
                    shiftCooldown.put(player.getName(), System.currentTimeMillis() + (30 * 1000));
                    duration.put(player.getName(), System.currentTimeMillis() + (4 * 1000));
                    player.sendMessage(ChatColor.GREEN + "Trueshot activated"); //left click skill
                }
            }
        }
    }

    @EventHandler
    public void ShootEvent(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            String uuid = player.getUniqueId().toString();
            SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
            if (smPlayer.getClassType() == ClassType.ARCHER) {
                World world = player.getWorld();
                Location origin = player.getLocation();  //player origin
                Vector direction = origin.getDirection();
                if (duration.get(player.getName()) == null)
                    return;
                if (duration.get(player.getName()) > System.currentTimeMillis()) {
                    Arrow arrow = (Arrow) event.getProjectile();
                    Vector vec = player.getEyeLocation().getDirection();
                    arrow.setVelocity(vec.multiply(4));
                    arrow.setCritical(true);
                    for (int i = 0; i < 50; i++) {
                        Location trail = origin.add(direction);
                        world.spawnParticle(Particle.GLOW, trail, 10);
                    }
                }
            }
        }
    }
}



