package ragnaorok.Main;

import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;

public enum Tools {
    NONE,
    SWORD,
    AXE,
    FIRE_WAND,
    HOE,
    BOW,
    CROSSBOW,
    SHIELD,
    SWORD_SHIELD,
    DUAL_AXE,
    DUAL_SWORD;


    public static Enum getMainHand(PlayerInventory inventory) {
        Enum mainHand = inventory.getItemInMainHand().getType();
        if (mainHand == Material.WOODEN_SWORD || mainHand == Material.STONE_SWORD || mainHand == Material.IRON_SWORD || mainHand == Material.DIAMOND_SWORD || mainHand == Material.NETHERITE_SWORD) {
            mainHand = SWORD;
        } else if (mainHand == Material.WOODEN_AXE || mainHand == Material.STONE_AXE || mainHand == Material.IRON_AXE || mainHand == Material.DIAMOND_AXE || mainHand == Material.NETHERITE_AXE) {
            mainHand = AXE;
        } else if (mainHand == Material.WOODEN_HOE || mainHand == Material.STONE_HOE || mainHand == Material.IRON_HOE || mainHand == Material.DIAMOND_HOE || mainHand == Material.NETHERITE_HOE) {
            mainHand = HOE;
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

    public static Enum getLoadOut(PlayerInventory inventory) {
        Enum loadout = null;
        if (getMainHand(inventory) == SWORD && getOffHand(inventory) == SWORD) {
            loadout = DUAL_SWORD;
        }
        else if (getMainHand(inventory) == AXE && getOffHand(inventory) == AXE) {
            loadout = DUAL_AXE;
        }
        else if (getMainHand(inventory) == HOE && getOffHand(inventory) == NONE) {
            loadout = HOE;
        }
        else if (getMainHand(inventory) == SWORD && getOffHand(inventory) == NONE) {
            loadout = SWORD;
        }
        else if (getMainHand(inventory) == SWORD && getOffHand(inventory) == Material.SHIELD) {
            loadout = SWORD_SHIELD;
        }
        else if (getMainHand(inventory) == Material.BLAZE_ROD && getOffHand(inventory) == NONE) {
            loadout = FIRE_WAND;
        }
        else {
            loadout = NONE;
        }

    return loadout;
    }

}

