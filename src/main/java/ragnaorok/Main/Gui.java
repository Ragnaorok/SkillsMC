package ragnaorok.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Gui implements Listener {
    private Inventory gui;

    public void openNewGui(Player player) {
        gui = Bukkit.createInventory(null, InventoryType.HOPPER);

        ItemStack item = new ItemStack(Material.GREEN_CONCRETE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Press This");
        item.setItemMeta(meta);

        gui.setItem(2, item);
        player.openInventory(gui);
    }

    @EventHandler
    public void guiClickEvent(InventoryClickEvent event) {
        if (!event.getInventory().equals(gui)) {
            return;
        }
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        switch (event.getSlot()) {
            case 2: {
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "You pressed it");
                break;
            }
        }
    }

    @EventHandler
    public void openGuiEvent(PlayerDropItemEvent event) {
       Item item =  event.getItemDrop();
           openNewGui(event.getPlayer());

    }
}
