package ragnaorok.Main.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ragnaorok.Main.Main;
import ragnaorok.Main.managers.SoulsManager;

public class SaveAllCommand implements CommandExecutor {

    public Main plugin;

    public SaveAllCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("saveAll").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (label.equalsIgnoreCase("saveAll")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "You cannot run this command!");
                    return true;
                }
                try {
                    SoulsManager.saveCurrencyFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return true;
    }
}
