package ragnaorok.Main.managers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import ragnaorok.Main.Currency;

import java.io.*;
import java.nio.file.FileSystemNotFoundException;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BountyManager {

    public static void saveBountyFile() throws Exception {
        File file = new File("currency.dat");
        boolean successful = true;
        if (!file.exists()) {
            successful = file.createNewFile();
        }

        if (!successful)
            return;

        try (ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
            output.writeObject(Currency.SOULS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void loadBountyFile() throws Exception {

        File file = new File("bounty.dat");
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
        int newAmount = getPlayerBounty(player) + amount;
        setPlayerBounty(player, newAmount);
    }

    public static void removePlayerBounty(OfflinePlayer player, int amount) {
        addBountyToPlayer(player, -amount);
    }

    public static void setPlayerBounty(OfflinePlayer player, int amount) {
        String playerUUID = player.getUniqueId().toString();
        Currency.BOUNTIES.put(playerUUID, amount);
    }

    public static int getPlayerBounty(OfflinePlayer player) {
        String playerUUID = player.getUniqueId().toString();
        // If they aren't already in the "database"
        Currency.BOUNTIES.putIfAbsent(playerUUID, 0);
        return Currency.BOUNTIES.get(playerUUID);
    }
}

