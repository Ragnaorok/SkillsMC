package ragnaorok.Main.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ragnaorok.Main.Main;
import ragnaorok.Main.managers.CurrencyManager;

public class SoulsCommand implements CommandExecutor {

    public Main plugin;

    public SoulsCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("souls").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("soulsmanager.use")) {
            if (args.length == 0) {
                sender.sendMessage("/souls <add:remove:set> <player>");
                return true;

            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("add")) {
                    sender.sendMessage("/souls add <player> <int>");
                } else if (args[0].equalsIgnoreCase("remove")) {
                    sender.sendMessage("/souls remove <player> <int>");
                } else if (args[0].equalsIgnoreCase("set")) {
                }
                sender.sendMessage("/souls set <player> <int>");
            } else if (args.length == 3) {
                @SuppressWarnings("deprecation")
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                int amount = Integer.parseInt(args[2]);
                if (args[0].equalsIgnoreCase("add")) {
                    if (player != null) {
                        CurrencyManager.addCurrencyToPlayer(player, amount);
                        sender.sendMessage("You have successfully added " + args[2] + " souls to "+ player.getName());
                    } else {
                        sender.sendMessage("could not be found");
                    }
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (player != null) {
                        CurrencyManager.setPlayerCurrency(player, amount);
                        sender.sendMessage("you have successfully set the player " + player.getName());
                    } else {
                        sender.sendMessage("player could not be found");
                    }
                }
            }
        } else {
            sender.sendMessage("no permission");
        }if (args.length == 2) {
            @SuppressWarnings("deprecation")
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            if (player != null || player.hasPlayedBefore()) {
                if (args[0].equalsIgnoreCase("get")) {
                    sender.sendMessage(player.getName() + " currently has " + CurrencyManager.getPlayerCurrency(player) + " souls");
                    return true;
                } else {
                    sender.sendMessage(player.getName() + "<amount>");
                }
            } else {
                sender.sendMessage(" player could not be found");
            }
        }
        return false;
    }
}



