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

import java.util.HashMap;
import java.util.Map;

public class PlayerDeathInventory implements Listener {
    private Inventory gui;
    Map<String, ItemStack[]> playerInventory = new HashMap<>();
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
        playerInventory.put(player.getName(), inventory);
        for (ItemStack item : inventory) {
            event.getDrops().remove(item);
        }
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
        gui = Bukkit.createInventory(null, 54, ChatColor.BLACK + "Items");
        ItemStack[] content = playerInventory.get(player.getName());
        gui.setContents(content);
        player.openInventory(gui);
        System.out.println("Death Gui Opened");
    }

}
