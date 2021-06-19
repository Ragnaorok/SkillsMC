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

    public static void saveBountyFile() throws FileSystemNotFoundException, IOException {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            File file = new File("BountyManager/bounty.dat");

            ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));

            UUID uuid = player.getUniqueId();

            if (Currency.BOUNTIES.get(player.getUniqueId().toString()) != null) {
                Currency.BOUNTIES.put(uuid, Currency.BOUNTIES.get(uuid));
            }
            try {
                output.writeObject(Currency.BOUNTIES);
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        if (Currency.BOUNTIES.get(player.getUniqueId()) != null) {
            Currency.BOUNTIES.put(player.getUniqueId(), Currency.BOUNTIES.get(player.getUniqueId()) + amount);
        } else {
            Currency.BOUNTIES.put(player.getUniqueId(), amount);
        }
    }

    public static void removePlayerBounty(OfflinePlayer player, int amount) {
        if (Currency.BOUNTIES.get(player.getUniqueId()) != null) {
            Currency.BOUNTIES.put(player.getUniqueId(), Currency.BOUNTIES.get(player.getUniqueId()) - amount);
        } else {
            Currency.BOUNTIES.put(player.getUniqueId(), amount);
        }
    }

    public static void setPlayerBounty(OfflinePlayer player, int amount) {

        Currency.BOUNTIES.put(player.getUniqueId(), amount);
    }

    public static int getPlayerBounty(OfflinePlayer player) {
        if (Currency.BOUNTIES.get(player.getUniqueId()) != null) {
            return Currency.BOUNTIES.get(player.getUniqueId());
        } else {
            return 0;
        }
    }
}

