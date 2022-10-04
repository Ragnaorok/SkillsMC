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
            //Create Guidebook
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bookmeta = (BookMeta) book.getItemMeta();
            bookmeta.setAuthor(ChatColor.RED + "Ragnarok");
            bookmeta.setTitle(ChatColor.DARK_PURPLE + "Guide Book");
            ArrayList<String> pages = new ArrayList<>();

            //Page 1
            pages.add(ChatColor.BOLD + "INDEX:\n"
                            + (ChatColor.BLACK + "pages 2-6: Classes\n")
                            + (ChatColor.BLACK + "page 7: Souls\n")
                            + (ChatColor.BLACK + "page 8: Bounties"));

            //Page 2
            pages.add(ChatColor.BOLD + "CLASSES:" +
                    "\nThere are 4 classes in RagnarokMC each with their own skills and abilities \n"
                    + (ChatColor.RED + "\n Warrior  ") + (ChatColor.YELLOW + "Cleric")
                    + (ChatColor.LIGHT_PURPLE + "\n Mage   ") + (ChatColor.DARK_GREEN + " Archer"));
            //Page 3
            pages.add(ChatColor.BOLD   + "Warrior:\n"
                    + (ChatColor.RED   + "Weapon:Sword ")
                    + (ChatColor.GREEN + "Skill: Bleed\n")
                    + (ChatColor.RED   + "Weapon:Shield\n")
                    + (ChatColor.GREEN + "Skill: Brace\n")
                    + (ChatColor.RED   + "Dual Weapon:Sword\n")
                    +(ChatColor.GREEN + "Skill: Sweep"));

            //Page 4
            pages.add(ChatColor.BOLD   + "Cleric:\n"
                    + (ChatColor.RED   + "Weapon:Hoe\n")
                    + (ChatColor.GREEN + "Skill: Hallowed Ground\n")
                    + (ChatColor.RED   + "Alt Weapon:Lightning Rod\n")
                    + (ChatColor.GREEN + "Skill: Energize"));
            //Page 5
            pages.add(ChatColor.BOLD   + "Mage:\n"
                    + (ChatColor.RED   + "Weapon:Blaze Rod\n")
                    + (ChatColor.GREEN + "Skill: Fire Ball\n")
                    + (ChatColor.RED   + "Weapon:Skeleton Skull\n")
                    + (ChatColor.GREEN + "Skill: Eldritch Blast"));
            //Page 6
            pages.add(ChatColor.BOLD   + "Archer:\n"
                    + (ChatColor.RED   + "Weapon:Bow\n")
                    + (ChatColor.GREEN + "Skill: Trueshot\n")
                    + (ChatColor.RED   + "Weapon:Crossbow\n")
                    + (ChatColor.GREEN + "Skill: Backstep"));
            //Page 7
            pages.add(ChatColor.BOLD   + "SOULS:\n"
                    + (ChatColor.BLACK + "Souls are a resource obtained by killing certain monsters\n")
                    + (ChatColor.BLACK + "When you gain enough souls you become stronger unlocking more skills\n")
                    + (ChatColor.BLACK + "Souls can be used as currency for special enchantments"));
            //Page 8
            pages.add(ChatColor.BOLD   + "BOUNTIES:\n"
                    + (ChatColor.BLACK + "Bounties are placed on players who kill others.\n")
                    + (ChatColor.BLACK + "The Chat will show bounty changes\n")
                    + (ChatColor.BLACK + "Killing a player with a bounty gives you souls based on their bounty\n"));

            bookmeta.setPages(pages);
            book.setItemMeta(bookmeta);
            player.getInventory().addItem(book);

            return true;
        }
        return false;
    }
}
