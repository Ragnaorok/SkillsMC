package ragnaorok.Main.progression;

import java.util.Collection;

/** Pure balancing rules; random variation is supplied by the caller for repeatable tests. */
public final class MobScaling {
    public static final int MAX_LEVEL = PlayerStats.GROWTH_CAP + 5;
    private MobScaling() {}
    public static int level(Collection<Integer> levels, int variation) {
        if (levels.isEmpty()) return 1;
        long sum = 0;
        int min = Integer.MAX_VALUE, max = 1;
        for (int level : levels) {
            level = Math.max(1, level);
            sum += level; min = Math.min(min, level); max = Math.max(max, level);
        }
        long result = Math.round((double) sum / levels.size()) + Math.clamp(variation, -5, 5);
        // Apply beginner protection AFTER variation. Equal-level groups use the ordinary roll.
        if (min != max) result = Math.min(result, (long) min + 5);
        return (int) Math.clamp(result, 1, MAX_LEVEL);
    }
    public static double healthMultiplier(int level) { return 1 + .03 * (Math.clamp(level, 1, MAX_LEVEL) - 1); }
    public static double damageMultiplier(int level) { return 1 + .01 * (Math.clamp(level, 1, MAX_LEVEL) - 1); }
    public static double armorChance(int level) { return Math.min(.25, Math.max(0, level - 1) * .0075); }
}
