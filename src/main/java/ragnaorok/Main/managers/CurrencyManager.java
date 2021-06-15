package ragnaorok.Main.managers;

import org.bukkit.OfflinePlayer;
import ragnaorok.Main.Currency;
import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CurrencyManager {
    public static final HashMap<UUID, Integer> souls = new HashMap<UUID, Integer>();

    public static void saveCurrencyFile() throws Exception {
        File file = new File("currency.dat");
        boolean successful = true;
        if (!file.exists()) {
            successful = file.createNewFile();
        }

        if (!successful)
            return;

        ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));

        try {
            output.writeObject(Currency.SOULS);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            output.close();
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
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            input.close();
        }
    }

    public static void addCurrencyToPlayer(OfflinePlayer player, int amount) {
        if (souls.get(player.getUniqueId()) != null) {
            souls.put(player.getUniqueId(), souls.get(player.getUniqueId()) + amount);
        } else {
            souls.put(player.getUniqueId(), amount);
        }
    }

    public static void removePlayerCurrency(OfflinePlayer player, int amount) {
        if (souls.get(player.getUniqueId()) != null) {
            souls.put(player.getUniqueId(), souls.get(player.getUniqueId()) - amount);
        } else {
            souls.put(player.getUniqueId(), amount);
        }
    }

    public static void setPlayerCurrency(OfflinePlayer player, int amount) {
        souls.put(player.getUniqueId(), amount);
    }

    public static int getPlayerCurrency(OfflinePlayer player) {
        if (souls.get(player.getUniqueId()) != null) {
            return souls.get(player.getUniqueId());
        }else{
            return 0;
        }
    }
}
