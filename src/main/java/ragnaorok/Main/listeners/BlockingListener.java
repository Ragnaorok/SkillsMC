package ragnaorok.Main.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class BlockingListener implements Listener { //A Skill that makes Shields usable on mainHand ((;
    Map<String, Long> cooldown = new HashMap<String, Long>();
    private boolean shielded = false;

    @EventHandler
    public void onBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Material type = player.getItemInHand().getType();
        PotionEffect brace = new PotionEffect(PotionEffectType.ABSORPTION, 20, 1);
        if (player.getItemInHand() == null) return;
        else {
            if (type == Material.SHIELD) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (cooldown.containsKey(player.getName())) {

                        if (cooldown.get(player.getName()) > System.currentTimeMillis()) {
                            long time = (cooldown.get(player.getName()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "Brace will be ready in " + time + " second(s)");
                            return;
                        }
                    }
                    cooldown.put(player.getName(), System.currentTimeMillis() + (3 * 1000));
                    player.sendMessage(ChatColor.GREEN + "Defensive Skill: Brace");
                    player.addPotionEffect((brace));
                }
            }
        }
    }
}


