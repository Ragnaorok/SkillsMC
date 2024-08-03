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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ragnaorok.Main.Constant;
import ragnaorok.Main.Main;
import ragnaorok.Main.SkillsMCPlayer;
import ragnaorok.Main.ClassType;

import java.util.HashMap;
import java.util.Map;

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

            // Check if the player is OP
            if (player.isOp()) {
                // Reset the player's class and open the GUI
                String uuid = player.getUniqueId().toString();
                SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
                resetPlayerStats(player, smPlayer);
                smPlayer.setClassType(ClassType.NONE);
                player.sendMessage(ChatColor.YELLOW + "Your class has been reset.");
                openGui(player);
            } else {
                // Deny access to non-OP players
                player.sendMessage(ChatColor.RED + "You have already chosen a class");
            }
            return true;
        }
        return false;
    }

    private void openGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.MAGIC + "Class Selection");

        Map<ClassType, Material> classMaterials = new HashMap<>();
        classMaterials.put(ClassType.WARRIOR, Material.IRON_SWORD);
        classMaterials.put(ClassType.MAGE, Material.BLAZE_ROD);
        classMaterials.put(ClassType.CLERIC, Material.SPLASH_POTION);
        classMaterials.put(ClassType.ARCHER, Material.BOW);

        int slot = 1;
        for (Map.Entry<ClassType, Material> entry : classMaterials.entrySet()) {
            ItemStack classItem = new ItemStack(entry.getValue());
            ItemMeta classMeta = classItem.getItemMeta();
            classMeta.setDisplayName(ChatColor.GREEN + entry.getKey().toString() + " Class");
            classItem.setItemMeta(classMeta);
            gui.setItem(slot, classItem);
            slot++;
        }

        player.openInventory(gui);
    }

    @EventHandler
    public void onClickEvent(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getItemMeta() == null) return;
        if (!ChatColor.stripColor(event.getView().getTitle()).equalsIgnoreCase("Class Selection")) return;
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null) return;

        Player player = (Player) event.getWhoClicked();
        String uuid = player.getUniqueId().toString();
        SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
        String className = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).replace(" Class", "").toUpperCase();

        try {
            ClassType classType = ClassType.valueOf(className);
            resetPlayerStats(player, smPlayer);
            smPlayer.setClassType(classType);
            player.sendMessage(ChatColor.GREEN + classType.toString() + " Class selected");

            //class-specific stat changes
            switch (classType) {
                case WARRIOR:
                    player.setMaxHealth(player.getMaxHealth() + 2);  // Increase max health by 1 heart (2 health points)
                    break;
                case ARCHER:
                    player.setWalkSpeed(player.getWalkSpeed() + 0.01f);  // Increase speed from 0.2 to 0.21 (4.0 to 4.2)
                    break;
                case CLERIC:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, false, false));
                    break;
                case MAGE:
                    player.setMaxHealth(player.getMaxHealth() - 2);  // Decrease max health by 1 heart (2 health points)
                    smPlayer.setManaShieldPercentage(0.1);  // 10% of damage absorbed by mana
                    break;
            }
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid class selected");
        }

        event.setCancelled(true);
        player.closeInventory();
    }

    private void resetPlayerStats(Player player, SkillsMCPlayer smPlayer) {
        player.setMaxHealth(20.0);  // Default max health is 20.0 (10 hearts)

        player.setWalkSpeed(0.2f);  // Default walk speed is 0.2 (4.0)

        smPlayer.setMana(10);      // Reset mana to base value

        // Remove any class-specific potion effects
        player.removePotionEffect(PotionEffectType.REGENERATION);
    }
}
