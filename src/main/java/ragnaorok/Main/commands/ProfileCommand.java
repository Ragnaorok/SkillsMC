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

public class ProfileCommand implements CommandExecutor, Listener {
    public Main plugin;

    public ProfileCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("profile").setExecutor(this);
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
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.BLACK + "Player Profile");
        String uuid = player.getUniqueId().toString();
        SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
        int currency_ = smPlayer.getCurrency();
        int souls_ = smPlayer.getSouls();
        int bounty_ = smPlayer.getBounty();
        double maxHearts_ = smPlayer.getMaxHearts();
        double speed_ = smPlayer.getMovementSpeed();
        double mana_ = smPlayer.getMana();

        ItemStack playerClass = new ItemStack(Material.PLAYER_HEAD);
        ItemStack currency = new ItemStack(Material.EMERALD);
        ItemStack souls = new ItemStack(Material.SOUL_LANTERN);
        ItemStack bounty = new ItemStack(Material.RED_DYE);
        ItemStack maxHearts = new ItemStack(Material.GOLDEN_APPLE);
        ItemStack speed = new ItemStack(Material.FEATHER);

        ItemMeta classItemMeta = playerClass.getItemMeta();
        ItemMeta currencyItemMeta = currency.getItemMeta();
        ItemMeta soulsItemMeta = souls.getItemMeta();
        ItemMeta bountyItemMeta = bounty.getItemMeta();
        ItemMeta maxHeartsItemMeta = maxHearts.getItemMeta();
        ItemMeta speedItemMeta = speed.getItemMeta();

        classItemMeta.setDisplayName(ChatColor.GREEN + "Class: " + smPlayer.getClassType().toString());
        currencyItemMeta.setDisplayName(ChatColor.GREEN + "Currency: " + currency_);
        soulsItemMeta.setDisplayName(ChatColor.GREEN + "Souls: " + souls_);
        bountyItemMeta.setDisplayName(ChatColor.GREEN + "Bounty: " + bounty_);
        maxHeartsItemMeta.setDisplayName(ChatColor.GREEN + "Max Hearts: " + maxHearts_);
        speedItemMeta.setDisplayName(ChatColor.GREEN + "Movement Speed: " + speed_);

        playerClass.setItemMeta(classItemMeta);
        currency.setItemMeta(currencyItemMeta);
        souls.setItemMeta(soulsItemMeta);
        bounty.setItemMeta(bountyItemMeta);
        maxHearts.setItemMeta(maxHeartsItemMeta);
        speed.setItemMeta(speedItemMeta);

        gui.setItem(22, playerClass);
        gui.setItem(32, currency);
        gui.setItem(31, souls);
        gui.setItem(13, bounty);
        gui.setItem(23, maxHearts);
        gui.setItem(24, speed);
        player.openInventory(gui);
    }

    @EventHandler
    public void onClickEvent(InventoryClickEvent event) { //Click event to see player profile
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (event.getCurrentItem().getItemMeta().getDisplayName().contains(":")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
        } else {
            return;
        }
    }
}
