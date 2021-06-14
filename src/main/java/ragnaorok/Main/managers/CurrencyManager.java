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

public class CurrencyManager {
    public static HashMap<UUID, Integer> souls = new HashMap<UUID, Integer>();
    public Main plugin;

    public CurrencyManager(Main Plugin) {
        this.plugin = plugin;
    }

    public void saveCurrencyFile() throws FileSystemNotFoundException, IOException {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            File file = new File("CurrencyData/currency.dat");

            ObjectOutputStream output = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));

            UUID uuid = player.getUniqueId();

            if (souls.get(player.getUniqueId().toString()) != null) {
                souls.put(uuid, souls.get(uuid));
            }
            try {
                output.writeObject(souls);
                output.flush();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @SuppressWarnings("unchecked")
    public void loadCurrencyFile() throws Exception {

        File file = new File("CurrencyData/currency.dat");

        if (file != null) {

            ObjectInputStream input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
            Object readObject = input.readObject();
            input.close();

            if (!(readObject instanceof HashMap)) {
                throw new IOException("Data is not a hashmap");
            }
            souls = (HashMap<UUID, Integer>) readObject;
            for (UUID key : souls.keySet()) {
                souls.put(key, souls.get(key));
                System.out.println(readObject);
            }
        }
    }

    public void addCurrencyToPlayer(OfflinePlayer player, int amount) {
        if (souls.get(player.getUniqueId()) != null) {
            souls.put(player.getUniqueId(), souls.get(player.getUniqueId()) + amount);
        } else {
            souls.put(player.getUniqueId(), amount);
        }
    }

    public void removePlayerCurrency(OfflinePlayer player, int amount) {
        if (souls.get(player.getUniqueId()) != null) {
            souls.put(player.getUniqueId(), souls.get(player.getUniqueId()) - amount);
        } else {
            souls.put(player.getUniqueId(), amount);
        }
    }

    public void setPlayerCurrency(OfflinePlayer player, int amount) {
        souls.put(player.getUniqueId(), amount);
    }

    public int getPlayerCurrency(OfflinePlayer player) {
        if (souls.get(player.getUniqueId()) != null) {
            return souls.get(player.getUniqueId());
        }else{
            return 0;
        }
    }
}
