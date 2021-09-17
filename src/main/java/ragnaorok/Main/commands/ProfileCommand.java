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
import ragnaorok.Main.ClassType;
import ragnaorok.Main.Main;
import ragnaorok.Main.SkillsMCPlayer;

public class ProfileCommand implements CommandExecutor {

    public Main plugin;

    public ProfileCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("profile").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("profile")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You cannot run this command!");
                return true;
            }
            Player player = (Player) sender;
            player.sendMessage(ChatColor.GREEN + SkillsMCPlayer.getClass(player).toString());
            return true;
        }
        return false;
    }
}


