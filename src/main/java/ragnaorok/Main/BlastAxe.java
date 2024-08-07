package ragnaorok.Main;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;
import java.util.UUID;

public class BlastAxe {
    public static void createBlastAxe(JavaPlugin plugin) {
        // Create the custom item
        ItemStack blastAxe = new ItemStack(Material.IRON_AXE);
        ItemMeta meta = blastAxe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§aBlast Axe");
            meta.setLore(Arrays.asList("§7A modified axe with an ignition key"));

            // Set the base stats
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", 4.5, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", -2.0, AttributeModifier.Operation.ADD_NUMBER));
            blastAxe.setItemMeta(meta);
        }

        // Define the crafting recipe
        NamespacedKey key = new NamespacedKey(plugin, "blast_axe");
        ShapedRecipe recipe = new ShapedRecipe(key, blastAxe);
        recipe.shape(" I ", " F ", "   ");
        recipe.setIngredient('I', Material.IRON_AXE);
        recipe.setIngredient('F', Material.FLINT_AND_STEEL);

        // Register the recipe
        plugin.getServer().addRecipe(recipe);
    }
}
