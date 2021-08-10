package ragnaorok.Main;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import static org.bukkit.Sound.*;

public class loginMessage implements Listener {
    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location loc = player.getLocation();
        Location soundLoc = loc.clone();
        player.sendMessage("Welcome to RagnarokMC V:1.9 (Alpha)" + ChatColor.ITALIC);
        world.playSound(soundLoc,BLOCK_BEACON_AMBIENT,73,1);
        world.playSound(soundLoc,ENTITY_EXPERIENCE_ORB_PICKUP,73,1);
    }
}
