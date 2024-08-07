package ragnaorok.Main.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ragnaorok.Main.Constant;
import ragnaorok.Main.Main;
import ragnaorok.Main.SkillsMCPlayer;

public class SetLevelCommand implements CommandExecutor {

    public Main plugin;

    public SetLevelCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("setLevel").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.isOp()) {
            if (args.length != 2) {
                sender.sendMessage("/setlevel <player> <level>");
                return true;
            }

            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage("Player not found");
                return true;
            }

            String uuid = player.getUniqueId().toString();
            SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
            try {
                int targetLevel = Integer.parseInt(args[1]);
                if (targetLevel <= smPlayer.getLevel()) {
                    sender.sendMessage("Target level must be higher than the current level.");
                    return true;
                }

                while (smPlayer.getLevel() < targetLevel) {
                    smPlayer.addExperience(smPlayer.getExpToNextLevel());
                }

                player.sendMessage("You have been leveled up to " + targetLevel);
                sender.sendMessage("Successfully leveled up " + player.getName() + " to " + targetLevel);
            } catch (NumberFormatException e) {
                sender.sendMessage("Invalid level. Please enter a number.");
            }
        } else {
            sender.sendMessage("No permission to use this command.");
        }
        return true;
    }
}
