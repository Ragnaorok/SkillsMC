package ragnaorok.Main.managers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import ragnaorok.Main.Main;

import java.io.*;
import java.nio.file.FileSystemNotFoundException;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BountyManager {
    public static HashMap<UUID, Integer> bounty = new HashMap<UUID, Integer>();
    public Main plugin;

    public BountyManager(Main Plugin) {
        this.plugin = plugin;
    }

    public void saveBountyFile() throws FileSystemNotFoundException, IOException {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            File file = new File("BountyManager/bounty.dat");

            ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));

            UUID uuid = player.getUniqueId();

            if (bounty.get(player.getUniqueId().toString()) != null) {
                bounty.put(uuid, bounty.get(uuid));
            }
            try {
                output.writeObject(bounty);
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @SuppressWarnings("unchecked")
    public void loadCurrencyFile() throws FileSystemNotFoundException, IOException, ClassNotFoundException {

        File file = new File("BountyData/bounty.dat");

        if (file != null) {

            ObjectInputStream input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
            Object readObject = input.readObject();
            input.close();

            if (!(readObject instanceof HashMap)) {
                throw new IOException("Data is not a hashmap");
            }
            bounty = (HashMap<UUID, Integer>) readObject;
            for (UUID key : bounty.keySet()) {
                bounty.put(key, bounty.get(key));
            }
        }
    }

    public void addBountyToPlayer(OfflinePlayer player, int amount) {
        if (bounty.get(player.getUniqueId()) != null) {
            bounty.put(player.getUniqueId(), bounty.get(player.getUniqueId()) + amount);
        } else {
            bounty.put(player.getUniqueId(), amount);
        }
    }

    public void removePlayerBounty(OfflinePlayer player, int amount) {
        if (bounty.get(player.getUniqueId()) != null) {
            bounty.put(player.getUniqueId(), bounty.get(player.getUniqueId()) - amount);
        } else {
            bounty.put(player.getUniqueId(), amount);
        }
    }

    public void setPlayerBounty(OfflinePlayer player, int amount) {

        bounty.put(player.getUniqueId(), amount);
    }

    public int getPlayerBounty(OfflinePlayer player) {
        if (bounty.get(player.getUniqueId()) != null) {
            return bounty.get(player.getUniqueId());
        }else{
            return 0;
        }
    }
}

