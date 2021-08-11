package ragnaorok.Main;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.io.*;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public enum ClassType {
    NONE(""),
    WARRIOR(ChatColor.RED + "WARRIOR"),
    MAGE(ChatColor.LIGHT_PURPLE + "MAGE"),
    CLERIC(ChatColor.YELLOW + "CLERIC"),
    ARCHER(ChatColor.GREEN + "ARCHER");

    private final String prefix;

    ClassType(String prefix) {
        this.prefix = prefix;
    }

    public static void setPlayerClassType(OfflinePlayer player, ClassType type) {
        String playerUUID = player.getUniqueId().toString();
        Constant.CLASSTYPE.put(playerUUID, type);
    }

    public static ClassType getPlayerClassType(OfflinePlayer player) {
        String playerUUID = player.getUniqueId().toString();
        Constant.CLASSTYPE.putIfAbsent(playerUUID, NONE);
        return Constant.CLASSTYPE.get(playerUUID);
    }

    public static void loadPlayerClassTypeFile() throws Exception {

        File file = new File("ClassType.dat");
        boolean successful = true;

        if (!file.exists()) {
            successful = file.createNewFile();
        }

        if (!successful)
            return;

        try (ObjectInputStream input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)))) {
            HashMap<String, ClassType> readObject = (HashMap<String, ClassType>) input.readObject();
            for (String key : readObject.keySet()) {
                ClassType type = readObject.get(key);
                Constant.CLASSTYPE.put(key, type);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void savePlayerClassTypeFile() throws Exception {
        File file = new File("ClassType.dat");
        boolean successful = true;
        if (!file.exists()) {
            successful = file.createNewFile();
        }

        if (!successful)
            return;

        try (ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
            output.writeObject(Constant.CLASSTYPE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}