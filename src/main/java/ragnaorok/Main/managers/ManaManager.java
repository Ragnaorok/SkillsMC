package ragnaorok.Main.managers;

import org.bukkit.OfflinePlayer;
import ragnaorok.Main.Constant;

import java.io.*;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ManaManager {
    public static void saveManaFile() throws Exception {
        File file = new File("mana.dat");
        boolean successful = true;
        if (!file.exists()) {
            successful = file.createNewFile();
        }

        if (!successful)
            return;

        try (ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
            output.writeObject(Constant.MANA);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void loadManaFile() throws Exception {

        File file = new File("mana.dat");
        boolean successful = true;

        if (!file.exists()) {
            successful = file.createNewFile();
        }

        if (!successful)
            return;

        try (ObjectInputStream input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)))) {
            HashMap<String, Integer> readObject = (HashMap<String, Integer>) input.readObject();
            for (String key : readObject.keySet()) {
                int val = readObject.get(key);
                Constant.MANA.put(key, val);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addMana(OfflinePlayer player, int amount) {
        int newAmount = getMana(player) + amount;
        setMana(player, newAmount);
    }

    public static void removePlayerMana(OfflinePlayer player, int amount) {
        addMana(player, -amount);
    }

    public static void setMana(OfflinePlayer player, int amount) {
        String playerUUID = player.getUniqueId().toString();
        Constant.BOUNTIES.put(playerUUID, amount);
    }

    public static int getMana(OfflinePlayer player) {
        String playerUUID = player.getUniqueId().toString();
        Constant.BOUNTIES.putIfAbsent(playerUUID, 10);
        return Constant.BOUNTIES.get(playerUUID);
    }
}
