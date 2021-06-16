package ragnaorok.Main.listeners;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.SeaPickle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Sound.*;

public class BlockingListener implements Listener { //A Skill that makes Shields usable on mainHand ((;
    Map<String, Long> cooldown = new HashMap<String, Long>();

    @EventHandler
    public void onBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location loc = player.getLocation();
        Location particleLoc = loc.clone();
        Location ploc = loc.clone();
        Material type = player.getItemInHand().getType();
        PotionEffect brace = new PotionEffect(PotionEffectType.ABSORPTION, 25, 1);
        PotionEffect overwhelm = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30, 3);
        if (player.getItemInHand() == null) return;

        if (type == Material.SHIELD) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (cooldown.containsKey(player.getName())) {
                    if (cooldown.get(player.getName()) > System.currentTimeMillis()) {
                        long time = (cooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                        player.sendMessage(ChatColor.DARK_GRAY + "Brace will be ready in " + time + " second(s)");
                        return;
                    }
                }
                cooldown.put(player.getName(), System.currentTimeMillis() + (5 * 1000));
                player.sendMessage(ChatColor.GREEN + "Defensive Skill: Brace");
                player.addPotionEffect((brace));
                for (int i = 0; i < 360; i += 5) { //Magic Circle
                    particleLoc.setY(ploc.getY() + 1);
                    particleLoc.setZ(ploc.getZ() + Math.sin(i));
                    particleLoc.setX(ploc.getX() + Math.cos(i));
                    world.spawnParticle(Particle.SPELL_INSTANT, particleLoc, 1);
                }
            } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) { //Unfinished Stub
                {
                    if (cooldown.containsKey(player.getName())) {

                        if (cooldown.get(player.getName()) > System.currentTimeMillis()) {
                            return;
                        }
                    }
                }
                cooldown.put(player.getName(), System.currentTimeMillis() + (3 * 1000));
                player.sendMessage(ChatColor.GREEN + "Offensive Skill: Overwhelm");
                player.addPotionEffect((overwhelm));
                if (player.getFacing() == BlockFace.NORTH || player.getFacing() == BlockFace.SOUTH) {
                    for (int i = 0; i < 3; i++) {
                        world.spawnParticle(Particle.CLOUD, particleLoc, 10);
                        for (int j = 0; j < 2; j++) {
                            world.spawnParticle(Particle.CLOUD, particleLoc, 10);
                        }
                        for (int j = 0; j < 2; j++) {
                            world.spawnParticle(Particle.CLOUD, particleLoc, 10);
                        }
                    }
                }
            }
        }
    }
}


