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
import ragnaorok.Main.Constant;
import ragnaorok.Main.Main;
import ragnaorok.Main.SkillsMCPlayer;

import static ragnaorok.Main.ClassType.*;

public class ClassCommand implements CommandExecutor, Listener {

    public Main plugin;

    public ClassCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("class").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("class")) {
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
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.MAGIC + "Class Selection");

        ItemStack warrior_Class = new ItemStack(Material.IRON_SWORD);
        ItemMeta warrior = warrior_Class.getItemMeta();
        warrior.setDisplayName(ChatColor.GREEN + "Warrior Class");
        warrior_Class.setItemMeta(warrior);
        gui.setItem(1, warrior_Class);

        ItemStack mage_Class = new ItemStack(Material.BLAZE_ROD);
        ItemMeta mage = mage_Class.getItemMeta();
        mage.setDisplayName(ChatColor.GREEN + "Mage Class");
        mage_Class.setItemMeta(mage);
        gui.setItem(2, mage_Class);

        ItemStack cleric_Class = new ItemStack(Material.SPLASH_POTION);
        ItemMeta cleric = cleric_Class.getItemMeta();
        cleric.setDisplayName(ChatColor.GREEN + "Cleric Class");
        cleric_Class.setItemMeta(cleric);
        gui.setItem(3, cleric_Class);

        ItemStack archer_Class = new ItemStack(Material.BOW);
        ItemMeta archer = archer_Class.getItemMeta();
        archer.setDisplayName(ChatColor.GREEN + "Archer Class");
        archer_Class.setItemMeta(archer);
        gui.setItem(4, archer_Class);
        player.openInventory(gui);
    }
    //TODO Clean up repeating code below this
    @EventHandler
    public void onClickEvent(InventoryClickEvent event) { //Click event to set class
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (!ChatColor.stripColor(event.getView().getTitle()).equalsIgnoreCase("Class Selection")) return;
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null) return;

        if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Warrior Class")) {
            Player player = (Player) event.getWhoClicked();
            String uuid = player.getUniqueId().toString();
            SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
            smPlayer.setClassType(WARRIOR);
            System.out.println("Warrior Class selected");
            event.setCancelled(true);
            player.closeInventory();
            return;
        }
        if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Mage Class")) {
            Player player = (Player) event.getWhoClicked();
            String uuid = player.getUniqueId().toString();
            SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
            smPlayer.setClassType(MAGE);
            System.out.println("Mage Class selected");
            event.setCancelled(true);
            player.closeInventory();
            return;
        }
        if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Cleric Class")) {
            Player player = (Player) event.getWhoClicked();
            String uuid = player.getUniqueId().toString();
            SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
            smPlayer.setClassType(CLERIC);
            System.out.println("Cleric Class selected");
            event.setCancelled(true);
            player.closeInventory();
            return;
        }
        if (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Archer Class")) {
            Player player = (Player) event.getWhoClicked();
            String uuid = player.getUniqueId().toString();
            SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
            smPlayer.setClassType(ARCHER);
            System.out.println("Archer Class selected");
            event.setCancelled(true);
            player.closeInventory();
            return;
        } else {
            return;
        }
    }
}
