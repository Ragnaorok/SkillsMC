package ragnaorok.Main;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import java.io.Serializable;

public class SkillsMCPlayer implements Serializable {
    private int mana, maxMana, souls, currency, bounty;
    private int level, experience;
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
        this.level = 1;  // Starting level
        this.experience = 0;  // Starting experience
    }

    // Getters and setters
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
        checkLevelUp();
    }

    public void addExperience(int experience) {
        this.experience += experience;
        checkLevelUp();
    }

    private void checkLevelUp() {
        int expToNextLevel = getExpToNextLevel();
        while (this.experience >= expToNextLevel) {
            this.experience -= expToNextLevel;
            this.level++;
            expToNextLevel = getExpToNextLevel();
            onLevelUp();
        }
    }

    public int getExpToNextLevel() {
        return this.level * 10;
    }

    private void onLevelUp() {
        player.sendMessage(ChatColor.GREEN + "You leveled up to level " + this.level + "!");
        Location ploc = player.getLocation();
        Location particleLoc = ploc.clone();
        for (int i = 0; i < 360; i += 5) {
            particleLoc.setY(ploc.getY()+2);
            particleLoc.setZ(ploc.getZ() + Math.sin(i));
            particleLoc.setX(ploc.getX() + Math.cos(i));
            player.spawnParticle(Particle.END_ROD, particleLoc, 1);
        }
    }
}
