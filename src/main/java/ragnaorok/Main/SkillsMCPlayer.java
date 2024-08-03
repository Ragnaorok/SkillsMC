package ragnaorok.Main;

import org.bukkit.entity.Player;

import java.io.Serializable;

public class SkillsMCPlayer implements Serializable {
    private int mana, maxMana, souls, currency, bounty;
    private ClassType classType;
    private transient Player player;
    private double manaShieldPercentage = 0.1;  // 10% damage absorbed by mana

    public SkillsMCPlayer() {
    }

    public SkillsMCPlayer(Player player) {
        String uuid = player.getUniqueId().toString();
    }

    public SkillsMCPlayer(Player player, int mana, int maxMana, int souls, int currency, int bounty, ClassType classType) {
        this.player = player;
        this.mana = maxMana;
        this.maxMana = maxMana;
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

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
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

    public double getMaxHearts() {
        return player.getMaxHealth() / 2.0;  // Each heart is 2 health points
    }

    public void setMaxHearts(double maxHearts) {
        player.setMaxHealth(maxHearts * 2.0);  // Convert hearts to health points
    }

    public double getMovementSpeed() {
        return player.getWalkSpeed() * 20;  // Walk speed is a value between 0.2 and 1.0, default is 0.2 (20%)
    }

    public void setMovementSpeed(double speed) {
        player.setWalkSpeed((float) (speed / 20.0));  // Convert speed percentage to walk speed
    }

    public double getManaShieldPercentage() {
        return manaShieldPercentage;
    }

    public void setManaShieldPercentage(double manaShieldPercentage) {
        this.manaShieldPercentage = manaShieldPercentage;
    }

    public void absorbDamageWithMana(double damage) {
        int manaCost = (int) (damage * manaShieldPercentage);
        if (mana >= manaCost) {
            mana -= manaCost;
            damage -= damage * manaShieldPercentage;
            player.damage(damage);
        } else {
            player.damage(damage);
        }
    }
}
