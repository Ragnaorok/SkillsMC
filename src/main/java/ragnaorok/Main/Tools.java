package ragnaorok.Main;

import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;

public enum Tools {
    NONE,
    SWORD,
    AXE;

    public static Enum getMainHand(PlayerInventory inventory) {
        Enum mainHand = inventory.getItemInMainHand().getType();
        if (mainHand == Material.WOODEN_SWORD || mainHand == Material.STONE_SWORD || mainHand == Material.IRON_SWORD || mainHand == Material.DIAMOND_SWORD || mainHand == Material.NETHERITE_SWORD) {
            mainHand = SWORD;
        } else if (mainHand == Material.WOODEN_AXE || mainHand == Material.STONE_AXE || mainHand == Material.IRON_AXE || mainHand == Material.DIAMOND_AXE || mainHand == Material.NETHERITE_AXE) {
            mainHand = AXE;
        } else if (mainHand == Material.AIR || mainHand == Material.CAVE_AIR ||mainHand == Material.VOID_AIR) {
            mainHand = NONE;
        }
        return mainHand;
    }

}

