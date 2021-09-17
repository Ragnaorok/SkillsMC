package ragnaorok.Main.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ragnaorok.Main.Constant;
import ragnaorok.Main.SkillsMCPlayer;

import java.io.*;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

// SoulsManager is responsible for any calls to "Souls" System

public class SoulsManager {

    // This saves ALL objects inside SOULS hashmap
    public static void saveSoulsFile() throws Exception {
        File file = new File("currency.dat");
        boolean successful = true;
        if (!file.exists()) {
            successful = file.createNewFile();
        }

        if (!successful)
            return;

        try (ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
            output.writeObject(Constant.SOULS);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void loadSoulsFile() throws Exception {

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
                Constant.SOULS.put(key, val);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addSoulsToPlayer(Player player, int amount) {
        int newAmount = SkillsMCPlayer.getSouls((Player) player) + amount;
        setPlayerSouls(player, newAmount);
    }

    public static void removePlayerSouls(Player player, int amount) {
        addSoulsToPlayer(player, -amount);
    }

    public static void setPlayerSouls(Player player, int amount) {
        String playerUUID = player.getUniqueId().toString();
        Constant.SOULS.put(playerUUID, amount);
    }

}