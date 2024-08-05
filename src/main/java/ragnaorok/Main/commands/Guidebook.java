package ragnaorok.Main.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import ragnaorok.Main.Main;

import java.util.ArrayList;

public class Guidebook implements CommandExecutor, Listener {
    public Main plugin;

    public Guidebook(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("guide").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("guide")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "You cannot run this command!");
                return true;
            }

            // Create Guidebook
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookmeta = (BookMeta) book.getItemMeta();
            bookmeta.setAuthor(ChatColor.RED + "Ragnarok");
            bookmeta.setTitle(ChatColor.DARK_PURPLE + "Guide Book");
            ArrayList<String> pages = new ArrayList<>();

            // Page 1: Index
            pages.add(ChatColor.BOLD + "INDEX:\n"
                    + ChatColor.BLACK + "pages 2-6: Classes\n"
                    + ChatColor.BLACK + "page 7: Souls\n"
                    + ChatColor.BLACK + "page 8: Bounties");

            // Page 2: Classes Overview
            pages.add(ChatColor.BOLD + "CLASSES:\n"
                    + "There are 4 classes each with their own skills and abilities.\n"
                    + ChatColor.RED + "Warrior  " + ChatColor.YELLOW + "Cleric\n"
                    + ChatColor.LIGHT_PURPLE + "Mage   " + ChatColor.DARK_GREEN + "Archer");

            // Page 3: Warrior
            pages.add(ChatColor.BOLD + "Warrior:\n"
                    + ChatColor.BLACK + "Passive: +1 Heart\n"
                    + ChatColor.RED + "Sword\n"
                    + ChatColor.GREEN + "Skill: Gale Slash\n"
                    + ChatColor.RED + "Shield\n"
                    + ChatColor.GREEN + "Skill: Shield Surf\n"
                    + ChatColor.RED + "Axe\n"
                    + ChatColor.GREEN + "Skill: Shockwave\n"
                    + ChatColor.RED + "Dual Sword\n"
                    + ChatColor.GREEN + "Skill: Blade Dance");

            // Page 4: Cleric
            pages.add(ChatColor.BOLD + "Cleric:\n"
                    + ChatColor.BLACK + "Passive: Rejuvenation\n"
                    + ChatColor.RED + "Hoe\n"
                    + ChatColor.GREEN + "Skill: Smite/Heal\n"
                    + ChatColor.RED + "Breeze Rod\n"
                    + ChatColor.GREEN + "Skill: Energize\n");

            // Page 5: Mage
            pages.add(ChatColor.BOLD + "Mage:\n"
                    + ChatColor.BLACK + "Passive: -1 Heart & Mana Shield\n"
                    + ChatColor.RED + "Blaze Rod\n"
                    + ChatColor.GREEN + "Skill: Fireball\n"
                    + ChatColor.RED + "Skill: Lava Eruption\n");

            // Page 6: Archer
            pages.add(ChatColor.BOLD + "Archer:\n"
                    + ChatColor.BLACK + "Passive: +.2 movement speed\n"
                    + ChatColor.RED + "Bow\n"
                    + ChatColor.GREEN + "Main Skill: Trueshot\n"
                    + ChatColor.GREEN + "Second Skill: Speed\n"
                    + ChatColor.RED + "Crossbow\n"
                    + ChatColor.GREEN + "Main Skill: Rapid Fire\n"
                    + ChatColor.GREEN + "Second Skill: Backstep\n");

            // Page 7: Souls
            pages.add(ChatColor.BOLD + "SOULS:\n"
                    + ChatColor.BLACK + "Souls are a resource obtained by killing certain monsters.\n"
                    + "Gain enough souls to become stronger and unlock more skills.\n"
                    + "Souls can be used as currency for special enchantments.");

            // Page 8: Bounties
            pages.add(ChatColor.BOLD + "BOUNTIES:\n"
                    + ChatColor.BLACK + "Bounties are placed on players who kill others.\n"
                    + "The chat will show bounty changes.\n"
                    + "Killing a player with a bounty gives you souls based on their bounty.");

            bookmeta.setPages(pages);
            book.setItemMeta(bookmeta);
            player.getInventory().addItem(book);

            return true;
        }
        return false;
    }
}
