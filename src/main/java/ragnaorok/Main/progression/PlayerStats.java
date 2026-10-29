package ragnaorok.Main.progression;

import org.bukkit.entity.Player;
import ragnaorok.Main.*;

/** Derived values only: no new fields in the legacy serialized player profile. */
public final class PlayerStats {
    public static final int GROWTH_CAP = 25;
    private PlayerStats() {}
    public static int growthLevel(int level) { return Math.max(1, Math.min(GROWTH_CAP, level)); }
    public static double bonusHealth(ClassType type, int level) {
        if (type == null || type == ClassType.NONE) return 0;
        return (growthLevel(level) / 5) * (type == ClassType.WARRIOR ? 2.0 : 1.0);
    }
    public static double maxHealth(ClassType type, int level) {
        return (type == ClassType.WARRIOR ? 22 : type == ClassType.MAGE ? 18 : 20) + bonusHealth(type, level);
    }
    public static double damageMultiplier(ClassType type, int level) {
        double rate = type == null ? 0 : switch (type) {
            case WARRIOR, CLERIC -> .01;
            case ARCHER -> .0125;
            case MAGE -> .015;
            default -> 0;
        };
        return 1 + (growthLevel(level) - 1) * rate;
    }
    public static double healingMultiplier(ClassType type, int level) {
        return type == ClassType.CLERIC ? 1 + (growthLevel(level) - 1) * .01 : 1;
    }
    public static void applyHealth(Player player, ClassType type, int level) {
        double maximum = maxHealth(type, level);
        if (player.getHealth() > maximum) player.setHealth(maximum);
        player.setMaxHealth(maximum);
    }
    public static SkillsMCPlayer profile(Player player) {
        return Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString());
    }
    public static double damage(Player player, double base) {
        SkillsMCPlayer p = profile(player);
        return base * (p == null ? 1 : damageMultiplier(p.getClassType(), p.getLevel()));
    }
    public static double healing(Player player, double base) {
        SkillsMCPlayer p = profile(player);
        return base * (p == null ? 1 : healingMultiplier(p.getClassType(), p.getLevel()));
    }
}
