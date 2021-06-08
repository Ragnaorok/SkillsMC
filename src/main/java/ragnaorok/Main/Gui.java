package ragnaorok.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Gui implements Listener {
    private Inventory gui;

    public void openNewGui(Player player) {
        gui = Bukkit.createInventory(null,9,ChatColor.AQUA + "Slayer Skills");

        ItemStack blood_Lust_Skill = new ItemStack(Material.END_CRYSTAL);
        ItemMeta blood_Lust = blood_Lust_Skill.getItemMeta();
        blood_Lust.setDisplayName(ChatColor.DARK_RED + "Slayer Skill: Blood Lust");
        blood_Lust_Skill.setItemMeta(blood_Lust);
        gui.setItem(2, blood_Lust_Skill);

        ItemStack second_Wind_Skill = new ItemStack(Material.END_CRYSTAL);
        ItemMeta second_Wind = second_Wind_Skill.getItemMeta();
        second_Wind.setDisplayName(ChatColor.GREEN + "Slayer Skill: Second Wind");
        second_Wind_Skill.setItemMeta(second_Wind);
        gui.setItem(3, second_Wind_Skill);

        player.openInventory(gui);
    }

    public ItemStack giveItem(String name){
        ItemStack blood_Lust_Skill = new ItemStack(Material.END_CRYSTAL);
        ItemMeta blood_Lust = blood_Lust_Skill.getItemMeta();
        blood_Lust.setDisplayName(ChatColor.DARK_RED + "Slayer Skill: Blood Lust");
        blood_Lust_Skill.setItemMeta(blood_Lust);
        return blood_Lust_Skill;
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
                player.sendMessage(ChatColor.GREEN + "Slayer Skill Selected");
                break;
            }
        }
    }

    @EventHandler
    public void openGuiEvent(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        openNewGui(event.getPlayer());

    }
}
