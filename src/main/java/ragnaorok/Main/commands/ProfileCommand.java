package ragnaorok.Main.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ragnaorok.Main.Constant;
import ragnaorok.Main.Main;
import ragnaorok.Main.SkillsMCPlayer;

import java.util.Objects;

public class ProfileCommand implements CommandExecutor {
    public Main plugin;
    private Inventory gui;

    public ProfileCommand(Main plugin) {
        this.plugin = plugin;
        Objects.requireNonNull(plugin.getCommand("profile")).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("profile")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You cannot run this command!");
                return true;
            }
            Player player = (Player) sender;
            ProfileGUI(player);
            return true;
        }
        return false;
    }

    public void ProfileGUI(Player player) {
        gui = Bukkit.createInventory(null, 54, ChatColor.BLACK + "Player Profile");
        String uuid = player.getUniqueId().toString();
        SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
        Integer currency_ = smPlayer.getCurrency();
        Integer souls_ = smPlayer.getSouls();
        Integer bounty_ = smPlayer.getBounty();

        ItemStack playerClass = new ItemStack(Material.PLAYER_HEAD);
        ItemStack currency = new ItemStack(Material.EMERALD);
        ItemStack souls = new ItemStack(Material.SOUL_FIRE);
        ItemStack bounty = new ItemStack(Material.RED_DYE);

        ItemMeta classItemMeta = playerClass.getItemMeta();
        ItemMeta currencyItemMeta = currency.getItemMeta();
        ItemMeta soulsItemMeta = souls.getItemMeta();
        ItemMeta bountyItemMeta = bounty.getItemMeta();

        classItemMeta.setDisplayName(ChatColor.GREEN + smPlayer.getClassType().toString());
        currencyItemMeta.setDisplayName(ChatColor.GREEN + currency_.toString());
        soulsItemMeta.setDisplayName(ChatColor.GREEN + souls_.toString());
        bountyItemMeta.setDisplayName(ChatColor.GREEN + bounty_.toString());

        playerClass.setItemMeta(classItemMeta);
        currency.setItemMeta(currencyItemMeta);
        souls.setItemMeta(soulsItemMeta);
        bounty.setItemMeta(bountyItemMeta);

        gui.setItem(22, playerClass);
        gui.setItem(24, currency);
        gui.setItem(31, souls);
        gui.setItem(13, bounty);

    }
}


