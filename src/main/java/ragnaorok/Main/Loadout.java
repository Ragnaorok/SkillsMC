package ragnaorok.Main;

import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;

public enum Loadout {
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
    MACE,
    DUAL_SWORD;

    public static Loadout getMainHand(PlayerInventory inventory) {
        Material mainHand = inventory.getItemInMainHand().getType();
        if (mainHand == Material.WOODEN_SWORD || mainHand == Material.STONE_SWORD || mainHand == Material.IRON_SWORD || mainHand == Material.DIAMOND_SWORD || mainHand == Material.NETHERITE_SWORD) {
            return SWORD;
        } else if (mainHand == Material.WOODEN_AXE || mainHand == Material.STONE_AXE || mainHand == Material.IRON_AXE || mainHand == Material.DIAMOND_AXE || mainHand == Material.NETHERITE_AXE) {
            return AXE;
        } else if (mainHand == Material.WOODEN_HOE || mainHand == Material.STONE_HOE || mainHand == Material.IRON_HOE || mainHand == Material.DIAMOND_HOE || mainHand == Material.NETHERITE_HOE) {
            return HOE;
        } else if (mainHand == Material.BOW) {
            return BOW;
        } else if (mainHand == Material.CROSSBOW) {
            return CROSSBOW;
        } else if (mainHand == Material.BLAZE_ROD) {
            return FIRE_WAND;
        } else if (mainHand == Material.AIR || mainHand == Material.CAVE_AIR || mainHand == Material.VOID_AIR) {
            return NONE;
        }
        return NONE;
    }

    public static Loadout getOffHand(PlayerInventory inventory) {
        Material offHand = inventory.getItemInOffHand().getType();
        if (offHand == Material.WOODEN_SWORD || offHand == Material.STONE_SWORD || offHand == Material.IRON_SWORD || offHand == Material.DIAMOND_SWORD || offHand == Material.NETHERITE_SWORD) {
            return SWORD;
        } else if (offHand == Material.WOODEN_AXE || offHand == Material.STONE_AXE || offHand == Material.IRON_AXE || offHand == Material.DIAMOND_AXE || offHand == Material.NETHERITE_AXE) {
            return AXE;
        } else if (offHand == Material.SHIELD) {
            return SHIELD;
        } else if (offHand == Material.AIR || offHand == Material.CAVE_AIR || offHand == Material.VOID_AIR) {
            return NONE;
        }
        return NONE;
    }

    public static Loadout getLoadOut(PlayerInventory inventory) {
        Loadout mainHand = getMainHand(inventory);
        Loadout offHand = getOffHand(inventory);

        if (mainHand == SWORD && offHand == SWORD) {
            return DUAL_SWORD;
        } else if (mainHand == AXE && offHand == AXE) {
            return DUAL_AXE;
        } else if (mainHand == HOE && offHand == NONE) {
            return HOE;
        } else if (mainHand == SWORD && offHand == NONE) {
            return SWORD;
        } else if (mainHand == SWORD && offHand == SHIELD) {
            return SWORD_SHIELD;
        } else if (mainHand == FIRE_WAND && offHand == NONE) {
            return FIRE_WAND;
        } else if (mainHand == BOW && offHand == NONE) {
            return BOW;
        } else if (mainHand == CROSSBOW && offHand == NONE) {
            return CROSSBOW;
        } else if (mainHand == NONE && offHand == NONE) {
            return NONE;
        }
        return NONE;
    }
}
