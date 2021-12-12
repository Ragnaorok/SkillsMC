package ragnaorok.Main;

import org.bukkit.ChatColor;

import java.io.*;


public enum ClassType  implements  Serializable{
    NONE(""),
    WARRIOR(ChatColor.RED + "WARRIOR"),
    MAGE(ChatColor.LIGHT_PURPLE + "MAGE"),
    CLERIC(ChatColor.YELLOW + "CLERIC"),
    ARCHER(ChatColor.GREEN + "ARCHER");

    private final String prefix;

    ClassType(String prefix) {
        this.prefix = prefix;
    }

}