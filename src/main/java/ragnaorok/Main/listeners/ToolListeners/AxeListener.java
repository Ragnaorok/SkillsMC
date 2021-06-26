package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class AxeListener implements Listener {
    Map<String, Long> leftCooldown = new HashMap<String, Long>();
    Map<String, Long> shiftCooldown = new HashMap<String, Long>();
    Map<String, Long> duration = new HashMap<String, Long>();

    @EventHandler
    public void test(PlayerInteractEvent event) {
        Player player = event.getPlayer();  //player
        Material type = player.getItemInHand().getType();
        if (player.getItemInHand() == null) return;
        if (type == Material.STONE_AXE || type == Material.IRON_AXE || type == Material.DIAMOND_AXE || type == Material.NETHERITE_AXE) {
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
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
                    player.sendMessage(ChatColor.GREEN + "Blight activated"); //left click skill
                }
            }
        }
    }

    @EventHandler
    public void Blight(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Monster) {
            Player player = (Player) event.getDamager();
            Material type = player.getItemInHand().getType();
            if (type == Material.STONE_AXE || type == Material.IRON_AXE || type == Material.DIAMOND_AXE || type == Material.NETHERITE_AXE) {
                World world = player.getWorld();
                Monster monster = (Monster) event.getEntity();
                Location loc = monster.getLocation();
                Location mloc = loc.clone();
                if (duration.get(player.getName()) > System.currentTimeMillis()) {
                    EntityType Fang = EntityType.EVOKER_FANGS;
                    world.spawnEntity(mloc, Fang);
                    player.sendMessage("Blight");
                }
            }

        }
    }
}
