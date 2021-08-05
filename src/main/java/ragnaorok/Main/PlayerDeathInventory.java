package ragnaorok.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PlayerDeathInventory implements Listener {
    private Inventory gui;
    private final static Map<String, ItemStack[]> playerInventory = new HashMap<>();
    private JavaPlugin plugin;

    public PlayerDeathInventory(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public PlayerDeathInventory() {

    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ItemStack[] inventory = player.getInventory().getContents();
        Collections.shuffle(Arrays.asList(inventory));
        playerInventory.put(player.getName(), inventory);
        event.getDrops().clear();
    }

    @EventHandler
    public void playerSpawn(PlayerRespawnEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                openNewGui(event.getPlayer());
            }
        }.runTaskLater(plugin, 1);
    }

    public void openNewGui(Player player) {
        gui = Bukkit.createInventory(null, 27, ChatColor.BLACK + "Retrieve Items");
        ItemStack[] content = playerInventory.get(player.getName());
        for (int i = 0; i < gui.getSize(); i++) {
            if (content[i] != null) {
                gui.addItem(content[i]);
            }
        }
        player.openInventory(gui);
    }
}

