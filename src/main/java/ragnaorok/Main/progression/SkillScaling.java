package ragnaorok.Main.progression;

import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;

/** Snapshot multipliers on skill entities; ordinary attacks and potions are untouched. */
public final class SkillScaling implements Listener {
    private final java.util.function.Predicate<EntityType> inverted;
    public SkillScaling() { this(type -> Tag.ENTITY_TYPES_INVERTED_HEALING_AND_HARM.isTagged(type)); }
    SkillScaling(java.util.function.Predicate<EntityType> inverted) { this.inverted = inverted; }
    static final NamespacedKey DAMAGE = new NamespacedKey("skillsmc", "skill_damage");
    static final NamespacedKey HEALING = new NamespacedKey("skillsmc", "skill_healing");
    public static void markDamage(Entity entity, double multiplier) {
        entity.getPersistentDataContainer().set(DAMAGE, PersistentDataType.DOUBLE, multiplier);
    }
    public static void markHealing(Entity entity, double multiplier) {
        entity.getPersistentDataContainer().set(HEALING, PersistentDataType.DOUBLE, multiplier);
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void damage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Fireball)) return;
        Double multiplier = event.getDamager().getPersistentDataContainer().get(DAMAGE, PersistentDataType.DOUBLE);
        if (multiplier != null) event.setDamage(event.getDamage() * multiplier);
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void splash(PotionSplashEvent event) {
        Double multiplier = event.getPotion().getPersistentDataContainer().get(HEALING, PersistentDataType.DOUBLE);
        if (multiplier == null) return;
        // Replace only our Hallowed Ground potion, so healing can exceed vanilla's capped splash intensity.
        event.setCancelled(true);
        for (LivingEntity target : event.getAffectedEntities()) {
            double intensity = event.getIntensity(target);
            if (target.isDead() || intensity <= 0) continue;
            if (inverted.test(target.getType())) {
                double damage = Math.floor(6 * intensity + .5);
                if (event.getPotion().getShooter() instanceof Entity source) target.damage(damage, source);
                else target.damage(damage);
            } else {
                target.heal(Math.floor(4 * intensity + .5) * multiplier,
                        EntityRegainHealthEvent.RegainReason.MAGIC);
            }
        }
    }
}
