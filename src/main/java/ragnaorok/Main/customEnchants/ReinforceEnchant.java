package ragnaorok.Main.customEnchants;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ragnaorok.Main.Main;
import ragnaorok.Main.managers.SoulsManager;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


public class ReinforceEnchant implements CommandExecutor {
    public static final Enchantment REINFORCE = new EnchantmentWrapper("reinforce", "Reinforce", 1);
    public Main plugin;

    public ReinforceEnchant(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("reinforce").setExecutor(this);
    }

    public ReinforceEnchant() {

    }

    public static void register() {
        boolean registered = Arrays.stream(Enchantment.values()).collect(Collectors.toList()).contains(REINFORCE);
        if (!registered)
            registerEnchantment(REINFORCE);
    }

    public static void registerEnchantment(Enchantment enchantment) {
        boolean registered = true;
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(enchantment);
        } catch (Exception e) {
            registered = false;
            e.printStackTrace();
        }
        if (registered) {
            System.out.println("Enchantment has been registered");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("reinforce")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You cannot run this command!");
                return true;
            }
            Player player = (Player) sender;
            if (!(player.getInventory().getItemInMainHand().getType() == Material.SHIELD))
                return true;
            if (SoulsManager.getPlayerSouls(player) < 50) {
                player.sendMessage(ChatColor.RED + "You do not have enough souls");
                return true;
            }
            if (player.getLevel() < 30) {
                player.sendMessage(ChatColor.RED + "You do not have enough levels");
                return true;
            }
            SoulsManager.removePlayerSouls(player, 50);
            player.setLevel(20);
            player.getInventory().remove(Material.SHIELD);
            ItemStack item = new ItemStack(Material.SHIELD);
            item.addUnsafeEnchantment(REINFORCE, 1);
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("Reinforce");
            meta.setDisplayName("Shield");
            meta.setLore(lore);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);
            return true;
        }
        return true;
    }
}

