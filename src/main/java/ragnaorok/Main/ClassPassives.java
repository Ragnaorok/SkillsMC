package ragnaorok.Main;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class ClassPassives {
    private ClassPassives() { }

    /** Absolute values prevent stacking on reconnect, respawn or repeated selection. */
    public static void apply(Player player, ClassType type) {
        SkillsMCPlayer profile = ragnaorok.Main.progression.PlayerStats.profile(player);
        if (profile != null) ragnaorok.Main.progression.MageMana.prepare(profile);
        apply(player, type, profile == null ? 1 : profile.getLevel());
    }

    public static void apply(Player player, ClassType type, int level) {
        ragnaorok.Main.progression.PlayerStats.applyHealth(player, type, level);
        player.setWalkSpeed(type == ClassType.ARCHER ? 0.21f : 0.2f);
        PotionEffect regeneration = player.getPotionEffect(PotionEffectType.REGENERATION);
        // Match SkillsMC's permanent Regen I; leave ordinary buffs alone.
        boolean ownRegen = regeneration != null && regeneration.getAmplifier() == 0
                && !regeneration.hasParticles() && !regeneration.isAmbient()
                && (regeneration.isInfinite() || regeneration.getDuration() > 1000000000);
        if (type == ClassType.CLERIC) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, -1, 0, false, false));
        } else if (ownRegen) {
            player.removePotionEffect(PotionEffectType.REGENERATION);
        }
    }
}
