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

    public static void saveBountyFile() throws FileSystemNotFoundException, IOException {
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
    public static void loadCurrencyFile() throws Exception {

        File file = new File("currency.dat");
        boolean successful = true;

        if (!file.exists()) {
            successful = file.createNewFile();
        }

        if (!successful)
            return;

        ObjectInputStream input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
        try {
            Object readObject = input.readObject();
            System.out.println(readObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            input.close();
        }
    }

    public static void addBountyToPlayer(OfflinePlayer player, int amount) {
        if (bounty.get(player.getUniqueId()) != null) {
            bounty.put(player.getUniqueId(), bounty.get(player.getUniqueId()) + amount);
        } else {
            bounty.put(player.getUniqueId(), amount);
        }
    }

    public static void removePlayerBounty(OfflinePlayer player, int amount) {
        if (bounty.get(player.getUniqueId()) != null) {
            bounty.put(player.getUniqueId(), bounty.get(player.getUniqueId()) - amount);
        } else {
            bounty.put(player.getUniqueId(), amount);
        }
    }

    public static void setPlayerBounty(OfflinePlayer player, int amount) {

        bounty.put(player.getUniqueId(), amount);
    }

    public static int getPlayerBounty(OfflinePlayer player) {
        if (bounty.get(player.getUniqueId()) != null) {
            return bounty.get(player.getUniqueId());
        }else{
            return 0;
        }
    }
}

