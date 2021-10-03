package ragnaorok.Main;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.*;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.bukkit.Sound.*;

public class LoginListener implements Listener {
    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location loc = player.getLocation();
        Location soundLoc = loc.clone();
        player.sendMessage("Welcome to RagnarokMC V:1.0 (Beta)" + ChatColor.ITALIC);
        world.playSound(soundLoc, BLOCK_BEACON_AMBIENT, 73, 1);
        world.playSound(soundLoc, ENTITY_EXPERIENCE_ORB_PICKUP, 73, 1);
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { // adds/loads player to a global Hashmap when they join
        Player player = event.getPlayer();
        File file = new File(player.getUniqueId() + ".dat");

        if (!file.exists()) {  // case that file doesn't exist, creates a new SkillsMCPlayer object for new player and pushes to global hashmap
            SkillsMCPlayer smPlayer = new SkillsMCPlayer(player, 10, 0, 0, 0, ClassType.NONE);
            Constant.SKILLS_MC_PLAYER_HASH_MAP.put(player.getUniqueId().toString(), smPlayer);
            return;
        } else {
            try (ObjectInputStream input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)))) {
                SkillsMCPlayer smPlayer = (SkillsMCPlayer) input.readObject();
                smPlayer.setPlayer(player);
                Constant.SKILLS_MC_PLAYER_HASH_MAP.put(player.getUniqueId().toString(), smPlayer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) throws IOException { // saves a player.dat when they exit the server
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        File file = new File(player.getUniqueId() + ".dat");


        if (!file.exists()) {  // case that file doesn't exist, creates a new file
            file.createNewFile();
        }
        if (Constant.SKILLS_MC_PLAYER_HASH_MAP.containsKey(uuid)) {
            try (ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
                output.writeObject(Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}


