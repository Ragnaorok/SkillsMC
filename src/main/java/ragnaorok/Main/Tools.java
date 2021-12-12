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
    public static Enum getOffHand(PlayerInventory inventory) {
        Enum offHand = inventory.getItemInOffHand().getType();
        if (offHand == Material.WOODEN_SWORD || offHand == Material.STONE_SWORD || offHand == Material.IRON_SWORD || offHand == Material.DIAMOND_SWORD || offHand == Material.NETHERITE_SWORD) {
            offHand = SWORD;
        } else if (offHand == Material.WOODEN_AXE || offHand == Material.STONE_AXE || offHand == Material.IRON_AXE || offHand == Material.DIAMOND_AXE || offHand == Material.NETHERITE_AXE) {
            offHand = AXE;
        } else if (offHand == Material.AIR || offHand == Material.CAVE_AIR ||offHand == Material.VOID_AIR) {
            offHand = NONE;
        }
        return offHand;
    }

}

