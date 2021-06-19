package ragnaorok.Main.managers;

import org.bukkit.OfflinePlayer;
import ragnaorok.Main.Currency;
import java.io.*;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

// CurrencyManager is responsible for any calls to "Souls" System

public class CurrencyManager {

    // This saves ALL objects inside SOULS hashmap
    // TODO: for optimization reasons, perhaps consider making it save data per user rather than every user in one file
    public static void saveCurrencyFile() throws Exception {
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

    public static void loadCurrencyFile() throws Exception {

        File file = new File("currency.dat");
        boolean successful = true;

        if (!file.exists()) {
            successful = file.createNewFile();
        }

        if (!successful)
            return;

        try (ObjectInputStream input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)))) {
            HashMap<String, Integer> readObject = (HashMap<String, Integer>) input.readObject();
            // deepCopy this object because we don't want to keep a reference to this readObject
            // which we assume is a HashMap<String, Integer>
            for (String key : readObject.keySet()) {
                int val = readObject.get(key);
                Currency.SOULS.put(key, val);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addCurrencyToPlayer(OfflinePlayer player, int amount) {
        int newAmount = getPlayerCurrency(player) + amount;
        setPlayerCurrency(player, newAmount);
    }

    public static void removePlayerCurrency(OfflinePlayer player, int amount) {
        addCurrencyToPlayer(player, -amount);
    }

    public static void setPlayerCurrency(OfflinePlayer player, int amount) {
        String playerUUID = player.getUniqueId().toString();
        Currency.SOULS.put(playerUUID, amount);
    }

    public static int getPlayerCurrency(OfflinePlayer player) {
        String playerUUID = player.getUniqueId().toString();
        // If they aren't already in the "database"
        Currency.SOULS.putIfAbsent(playerUUID, 0);
        return Currency.SOULS.get(playerUUID);
    }
}