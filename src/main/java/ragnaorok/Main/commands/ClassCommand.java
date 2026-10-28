package ragnaorok.Main.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import ragnaorok.Main.*;
import java.util.ArrayList;

public final class ClassCommand implements CommandExecutor, Listener {
    private final Main plugin;
    private static final ClassType[] CHOICES = {ClassType.WARRIOR, ClassType.MAGE, ClassType.CLERIC, ClassType.ARCHER};
    private static final Material[] ICONS = {Material.IRON_SWORD, Material.WOODEN_HOE, Material.SPLASH_POTION, Material.BOW};

    public ClassCommand(Main plugin) { this.plugin = plugin; }

    private static final class Menu implements InventoryHolder {
        final ClassType selection;
        final Inventory inventory;
        Menu(ClassType selection) {
            this.selection = selection;
            inventory = Bukkit.createInventory(this, 9, selection == null ? "Class Selection" : "Confirm Class");
        }
        @Override public Inventory getInventory() { return inventory; }
    }

    private boolean permitted(Player player, SkillsMCPlayer profile) {
        return profile.getClassType() == ClassType.NONE
                ? player.hasPermission("skillsmc.class.select") : player.isOp();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) { sender.sendMessage("Run this command in game."); return true; }
        SkillsMCPlayer profile = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString());
        if (profile == null) { player.sendMessage("Your SkillsMC profile is unavailable."); return true; }
        if (!permitted(player, profile)) { player.sendMessage("You do not have permission to change class."); return true; }
        Menu menu = new Menu(null);
        for (int i = 0; i < CHOICES.length; i++) menu.inventory.setItem(i + 1, icon(ICONS[i], CHOICES[i].name()));
        player.openInventory(menu.inventory);
        return true;
    }

    private ItemStack icon(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + name);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Menu) event.setCancelled(true);
    }

    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Menu menu)) return;
        event.setCancelled(true); // Includes shift-clicks, hotbar swaps and the player's bottom inventory.
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getRawSlot() < 0 || event.getRawSlot() >= menu.inventory.getSize()) return;
        int slot = event.getRawSlot();
        if (menu.selection == null) {
            if (slot < 1 || slot > CHOICES.length) return;
            ClassType selection = CHOICES[slot - 1];
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (!player.isOnline() || player.getOpenInventory().getTopInventory() != menu.inventory) return;
                Menu confirmation = new Menu(selection);
                confirmation.inventory.setItem(2, icon(Material.LIME_WOOL, "Confirm " + selection.name() + " (keeps level and XP)"));
                confirmation.inventory.setItem(6, icon(Material.RED_WOOL, "Cancel"));
                player.openInventory(confirmation.inventory);
            });
            return;
        }
        if (slot != 2 && slot != 6) return;
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (!player.isOnline() || player.getOpenInventory().getTopInventory() != menu.inventory) return;
            player.closeInventory();
            if (slot == 6) return;
            SkillsMCPlayer profile = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString());
            if (profile == null || !permitted(player, profile)) { player.sendMessage("You cannot change class."); return; }
            ClassType previous = profile.getClassType();
            if (previous == menu.selection) { player.sendMessage("You already have that class."); return; }
            if (!ragnaorok.Main.data.ClassSelection.commit(profile, menu.selection,
                    () -> plugin.savePlayer(player.getUniqueId(), profile))) {
                player.sendMessage(ChatColor.RED + "Class change cancelled because your progress could not be saved.");
                return;
            }
            ragnaorok.Main.skills.SkillAccess.invalidate(player);
            ClassPassives.apply(player, menu.selection);
            ItemStack book = switch (menu.selection) {
                case WARRIOR -> createWarriorGuide();
                case ARCHER -> createArcherGuide();
                case MAGE -> createMageGuide();
                case CLERIC -> createClericGuide();
                default -> throw new IllegalStateException("Invalid class");
            };
            // Avoid dropping duplicate guidebooks or losing one when an inventory is full.
            if (player.getInventory().firstEmpty() >= 0) player.getInventory().addItem(book);
            else player.sendMessage("Inventory full. Use /guide for ability help.");
            player.sendMessage(ChatColor.GREEN + menu.selection.name() + " selected. Your progress has been preserved.");
        });
    }

    private ItemStack createWarriorGuide() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookmeta = (BookMeta) book.getItemMeta();
        bookmeta.setAuthor(ChatColor.RED + "Ragnarok");
        bookmeta.setTitle(ChatColor.DARK_PURPLE + "Warrior Guide");
        ArrayList<String> pages = new ArrayList<>();

        // Page 1: Warrior Overview
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

        bookmeta.setPages(pages);
        book.setItemMeta(bookmeta);
        return book;
    }

    private ItemStack createClericGuide() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookmeta = (BookMeta) book.getItemMeta();
        bookmeta.setAuthor(ChatColor.RED + "Ragnarok");
        bookmeta.setTitle(ChatColor.DARK_PURPLE + "Cleric Guide");
        ArrayList<String> pages = new ArrayList<>();

        // Page 1: Cleric Overview
        pages.add(ChatColor.BOLD + "Cleric:\n"
                + ChatColor.BLACK + "Passive: Rejuvenation\n"
                + ChatColor.RED + "Hoe\n"
                + ChatColor.GREEN + "Skill: Smite\n"
                + ChatColor.RED + "Mace\n"
                + ChatColor.GREEN + "Skill: Morning Star\n");

        bookmeta.setPages(pages);
        book.setItemMeta(bookmeta);
        return book;
    }

    private ItemStack createMageGuide() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookmeta = (BookMeta) book.getItemMeta();
        bookmeta.setAuthor(ChatColor.RED + "Ragnarok");
        bookmeta.setTitle(ChatColor.DARK_PURPLE + "Mage Guide");
        ArrayList<String> pages = new ArrayList<>();

        // Page 1: Mage Overview
        pages.add(ChatColor.BOLD + "Mage:\n"
                + ChatColor.BLACK + "Passive: -1 Heart\n"
                + ChatColor.RED + "Any hoe\n"
                + ChatColor.GREEN + "Left: Arcane Bolt (free)\nRight: selected spell\n"
                + ChatColor.RED + "Sneak-right: switch spell\nFireball: 20 mana\nEruption: 45 mana\n");

        bookmeta.setPages(pages);
        book.setItemMeta(bookmeta);
        return book;
    }

    private ItemStack createArcherGuide() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookmeta = (BookMeta) book.getItemMeta();
        bookmeta.setAuthor(ChatColor.RED + "Ragnarok");
        bookmeta.setTitle(ChatColor.DARK_PURPLE + "Archer Guide");
        ArrayList<String> pages = new ArrayList<>();

        // Page 1: Archer Overview
        pages.add(ChatColor.BOLD + "Archer:\n"
                + ChatColor.BLACK + "Passive: +5% walk speed\n"
                + ChatColor.RED + "Bow\n"
                + ChatColor.GREEN + "Main Skill: Trueshot\n"
                + ChatColor.GREEN + "Second Skill: Speed\n"
                + ChatColor.RED + "Crossbow\n"
                + ChatColor.GREEN + "Main Skill: Rapid Fire\n"
                + ChatColor.GREEN + "Second Skill: Backstep\n");

        bookmeta.setPages(pages);
        book.setItemMeta(bookmeta);
        return book;
    }
}
