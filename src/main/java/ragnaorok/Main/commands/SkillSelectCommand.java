package ragnaorok.Main.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ragnaorok.Main.Main;

public class SkillSelectCommand implements CommandExecutor, Listener {

    public SkillSelectCommand(Main main) {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("skill")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You cannot run this command!");
                return true;
            }
            Player player = (Player) sender;
            openGui(player);
            return true;
        }
        return false;
    }

    public void openGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.AQUA + "Slayer Skills");

        ItemStack blood_Lust_Skill = new ItemStack(Material.END_CRYSTAL);
        ItemMeta blood_Lust = blood_Lust_Skill.getItemMeta();
        blood_Lust.setDisplayName(ChatColor.GREEN + "Slayer Skill: Blood Lust");
        blood_Lust_Skill.setItemMeta(blood_Lust);
        gui.setItem(1, blood_Lust_Skill);
        player.openInventory(gui);
    }

    public ItemStack giveItem(String name) {
        ItemStack blood_Lust_Skill = new ItemStack(Material.END_CRYSTAL);
        ItemMeta blood_Lust = blood_Lust_Skill.getItemMeta();
        blood_Lust.setDisplayName(ChatColor.GREEN + "Slayer Skill: Blood Lust");
        blood_Lust_Skill.setItemMeta(blood_Lust);
        return  blood_Lust_Skill;
    }
    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (!ChatColor.stripColor(event.getView().getTitle().toString()).equalsIgnoreCase("Slayer Skills")) return;
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null) return;

        if (!ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Slayer Skill: Blood Lust")) {
            Player player = (Player) event.getWhoClicked();
            player.getInventory().addItem((giveItem(player.getName())));
            event.setCancelled(true);
            player.closeInventory();
            return;
        }
        return;
    }
}


