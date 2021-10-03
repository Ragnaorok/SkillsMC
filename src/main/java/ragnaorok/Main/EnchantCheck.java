package ragnaorok.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;

public class EnchantCheck implements CommandExecutor {
    public Main plugin;

    public EnchantCheck(Main plugin) {
        this.plugin = plugin;
        Objects.requireNonNull(plugin.getCommand("check")).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("check")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You cannot run this command!");
                return true;
            }
            Player player = (Player) sender;
            if (player.getItemInHand() != null) {
                if (player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasEnchants()) {
                    Map<Enchantment, Integer> enchantments = player.getItemInHand().getEnchantments();
                    for (Enchantment enchantment : enchantments.keySet()) {
                        player.sendMessage("Enchantment(s): " + enchantment.getName());
                    }
                }
            }
            return true;
        }
        return false;
    }
}
