package ragnaorok.Main.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ragnaorok.Main.Constant;
import ragnaorok.Main.Main;
import ragnaorok.Main.SkillsMCPlayer;

public class SoulsCommand implements CommandExecutor {

    public Main plugin;

    public SoulsCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("souls").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
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
                Player player = Bukkit.getPlayer(args[1]);
                String uuid = player.getUniqueId().toString();
                SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
                int amount = Integer.parseInt(args[2]);
                if (args[0].equalsIgnoreCase("add")) {
                    if (player != null) {
                        smPlayer.setSouls(smPlayer.getSouls() + amount);
                        sender.sendMessage("You have successfully added " + args[2] + " souls to "+ player.getName());
                    } else {
                        sender.sendMessage("could not be found");
                    }
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (player != null) {
                        smPlayer.setSouls(amount);
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
            Player player = Bukkit.getPlayer(args[1]);
            String uuid = player.getUniqueId().toString();
            SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
            if (player != null || player.hasPlayedBefore()) {
                if (args[0].equalsIgnoreCase("get")) {
                    sender.sendMessage(player.getName() + " currently has " + smPlayer.getSouls() + " souls");
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



