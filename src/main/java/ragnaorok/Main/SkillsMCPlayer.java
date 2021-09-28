package ragnaorok.Main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class SkillsMCPlayer implements Serializable {
    private int mana, souls, currency, bounty;
    private ClassType classType;
    private Player player;

    public SkillsMCPlayer() {

    }

    public SkillsMCPlayer(Player player) {
        String uuid = player.getUniqueId().toString();
    }

    public SkillsMCPlayer(Player player, int mana, int souls, int currency, int bounty, ClassType classType) {
        this.player = player;
        this.mana = mana;
        this.souls = souls;
        this.currency = currency;
        this.bounty = bounty;
        this.classType = classType;
    }


    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public int getSouls() {
        return souls;
    }

    public void setSouls(int souls) {
        this.souls = souls;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public int getBounty() {
        return bounty;
    }

    public void setBounty(int bounty) {
        this.bounty = bounty;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public Player getPlayer() {
        return player;
    }

    public String getUUID() {
        return player.getUniqueId().toString();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }


}

