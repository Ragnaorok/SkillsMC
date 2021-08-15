package ragnaorok.Main;

import org.bukkit.entity.Player;

import java.io.*;

public class SkillsMCPlayer implements Serializable {
    private int mana, souls, currency;
    private ClassType classType;
    private Player player;

    public SkillsMCPlayer() {
    }

    public SkillsMCPlayer(Player player) {
        player.getUniqueId();
    }

    public SkillsMCPlayer(int mana, int souls, int currency) {
        this.mana = mana;
        this.souls = souls;
        this.currency = currency;
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

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
