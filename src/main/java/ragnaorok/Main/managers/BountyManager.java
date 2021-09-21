package ragnaorok.Main.managers;

import org.bukkit.entity.Player;
import ragnaorok.Main.Constant;
import java.io.*;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BountyManager {

    public static void saveBountyFile() throws Exception {
        File file = new File("bounty.dat");
        boolean successful = true;
        if (!file.exists()) {
            successful = file.createNewFile();
        }

        if (!successful)
            return;

        try (ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
            output.writeObject(Constant.BOUNTIES);
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

        try (ObjectInputStream input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)))) {
            HashMap<String, Integer> readObject = (HashMap<String, Integer>) input.readObject();
            // deepCopy this object because we don't want to keep a reference to this readObject
            // which we assume is a HashMap<String, Integer>
            for (String key : readObject.keySet()) {
                int val = readObject.get(key);
                Constant.BOUNTIES.put(key, val);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void addBountyToPlayer(Player player, int amount) {
        int newAmount = getBounty(player) + amount;
        setPlayerBounty(player, newAmount);
    }

    public static void removePlayerBounty(Player player, int amount) {
        addBountyToPlayer(player, -amount);
    }

    public static void setPlayerBounty(Player player, int amount) {
        String playerUUID = player.getUniqueId().toString();
        Constant.BOUNTIES.put(playerUUID, amount);
    }


}

