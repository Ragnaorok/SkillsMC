package ragnaorok.Main;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.io.*;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public enum ClassType {
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