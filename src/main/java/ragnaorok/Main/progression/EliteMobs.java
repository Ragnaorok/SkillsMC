package ragnaorok.Main.progression;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.*;
import org.bukkit.attribute.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.*;

/** Persisted spawn rolls and event-driven abilities; no ticking mob scans. */
public final class EliteMobs implements Listener {
    public enum Trait { FRENZIED, ARMORED, VENOMOUS, LAST_STAND }
    static final NamespacedKey TRAIT = key("elite_trait"), ROLLED = key("elite_rolled"),
            TRIGGERED = key("elite_triggered"), UNTIL = key("elite_until"), VENOM = key("elite_venom_ready"), SPEED = key("elite_speed");
    private static NamespacedKey key(String name) { return new NamespacedKey("skillsmc", name); }
    private static final Set<EntityType> MELEE = EnumSet.of(EntityType.ZOMBIE, EntityType.ZOMBIE_VILLAGER,
            EntityType.HUSK, EntityType.DROWNED, EntityType.WITHER_SKELETON, EntityType.SPIDER,
            EntityType.CAVE_SPIDER, EntityType.SILVERFISH, EntityType.ENDERMITE, EntityType.VINDICATOR,
            EntityType.PIGLIN_BRUTE, EntityType.HOGLIN, EntityType.ZOGLIN, EntityType.RAVAGER);
    static List<Trait> choices(EntityType type) {
        return MELEE.contains(type) ? List.of(Trait.values()) : List.of(Trait.ARMORED, Trait.LAST_STAND);
    }
    static boolean wins(int level, double roll) { return level >= 10 && roll >= 0 && roll < .05; }
    public static Trait trait(Entity entity) {
        String value = entity.getPersistentDataContainer().get(TRAIT, PersistentDataType.STRING);
        if (value == null) return null;
        try { return Trait.valueOf(value); } catch (IllegalArgumentException ignored) { return null; }
    }
    public static String label(Entity entity) {
        Trait t = trait(entity);
        return t == null ? "" : switch(t) {
            case FRENZIED -> "Frenzied "; case ARMORED -> "Armored ";
            case VENOMOUS -> "Venomous "; case LAST_STAND -> "Last Stand ";
        };
    }
    public static int reward(Entity entity, int base) { return trait(entity) == null ? base : bonusXp(base); }
    static int bonusXp(int base) { return (int)Math.min(Integer.MAX_VALUE, Math.round(Math.max(0,base) * 1.5)); }
    static void roll(LivingEntity mob, double chance, int selection) {
        var data = mob.getPersistentDataContainer();
        if (data.has(ROLLED, PersistentDataType.BYTE)) return;
        data.set(ROLLED, PersistentDataType.BYTE, (byte)1);
        Integer level = MobLevels.level(mob);
        if (level == null || !MobLevels.HOSTILES.contains(mob.getType()) || !wins(level,chance)) return;
        var pool = choices(mob.getType());
        Trait t = pool.get(Math.floorMod(selection,pool.size()));
        data.set(TRAIT, PersistentDataType.STRING,t.name());
        if (t == Trait.ARMORED) speed(mob,-.10);
    }
    public static void roll(LivingEntity mob) {
        roll(mob,ThreadLocalRandom.current().nextDouble(),ThreadLocalRandom.current().nextInt(4));
    }
    static void speed(LivingEntity mob, double amount) {
        var attribute = mob.getAttribute(Attribute.MOVEMENT_SPEED);
        if (attribute == null) return;
        for (var modifier : new ArrayList<>(attribute.getModifiers()))
            if (modifier.getKey().equals(SPEED)) attribute.removeModifier(modifier);
        attribute.addModifier(new AttributeModifier(SPEED,amount,AttributeModifier.Operation.ADD_SCALAR));
    }
    public static void transfer(LivingEntity source, LivingEntity target) {
        var from = source.getPersistentDataContainer(); var to = target.getPersistentDataContainer();
        // A conversion never gets a fresh roll, even for pre-feature mobs.
        to.set(ROLLED,PersistentDataType.BYTE,(byte)1);
        Trait t = trait(source);
        if (t == null) return;
        to.set(TRAIT,PersistentDataType.STRING,t.name());
        Byte used = from.get(TRIGGERED,PersistentDataType.BYTE);
        if (used != null) to.set(TRIGGERED,PersistentDataType.BYTE,used);
        for (var key : List.of(UNTIL,VENOM)) {
            Long value = from.get(key,PersistentDataType.LONG);
            if(value != null) to.set(key,PersistentDataType.LONG,value);
        }
        if(t == Trait.ARMORED) speed(target,-.10);
        if(t == Trait.FRENZIED && used != null) speed(target,.15);
    }
    @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)
    public void mitigate(EntityDamageByEntityEvent event) {
        Trait t = trait(event.getEntity());
        if(t == Trait.ARMORED) event.setDamage(event.getDamage()*.85);
        if(t == Trait.LAST_STAND) {
            Long until=event.getEntity().getPersistentDataContainer().get(UNTIL,PersistentDataType.LONG);
            if(until != null && System.currentTimeMillis()<until) event.setDamage(event.getDamage()*.75);
        }
    }
    @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
    public void hurt(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof LivingEntity mob) || event.getFinalDamage()<=0) return;
        Trait t=trait(mob);
        if(t != Trait.FRENZIED && t != Trait.LAST_STAND) return;
        var data=mob.getPersistentDataContainer();
        if(data.has(TRIGGERED,PersistentDataType.BYTE)) return;
        var health=mob.getAttribute(Attribute.MAX_HEALTH);
        if(health == null) return;
        double remaining=mob.getHealth()-event.getFinalDamage();
        double threshold=t == Trait.FRENZIED ? .40 : .25;
        if(remaining<=0 || remaining>=health.getValue()*threshold) return;
        data.set(TRIGGERED,PersistentDataType.BYTE,(byte)1);
        if(t == Trait.FRENZIED) speed(mob,.15);
        else data.set(UNTIL,PersistentDataType.LONG,System.currentTimeMillis()+4000);
        mob.getWorld().spawnParticle(t == Trait.FRENZIED ? Particle.ANGRY_VILLAGER : Particle.ENCHANT,
                mob.getLocation().add(0,1,0),8,.3,.3,.3,.01);
        mob.getWorld().playSound(mob.getLocation(),Sound.ITEM_SHIELD_BLOCK,.6f,t == Trait.FRENZIED ? 1.5f : .7f);
    }
    @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
    public void venom(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof LivingEntity mob) || trait(mob)!=Trait.VENOMOUS
                || !(event.getEntity() instanceof LivingEntity victim) || event.getFinalDamage()<=0
                || (event.getCause()!=EntityDamageEvent.DamageCause.ENTITY_ATTACK
                    && event.getCause()!=EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) return;
        long now=System.currentTimeMillis();
        Long ready=mob.getPersistentDataContainer().get(VENOM,PersistentDataType.LONG);
        if(ready != null && now<ready) return;
        mob.getPersistentDataContainer().set(VENOM,PersistentDataType.LONG,now+8000);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON,40,0));
        victim.getWorld().spawnParticle(Particle.HAPPY_VILLAGER,victim.getLocation().add(0,1,0),6,.2,.2,.2,0);
    }
}
