package ragnaorok.Main.progression;

import java.util.*;
import ragnaorok.Main.*;

/** Main-thread mana accounting; uses the existing serialized mana fields. */
public final class MageMana {
    public enum Spell {
        BOLT(0, 1000), FIREBALL(20, 3000), ERUPTION(45, 10000);
        public final int cost;
        public final long cooldown;
        Spell(int cost, long cooldown) { this.cost = cost; this.cooldown = cooldown; }
    }
    private static final Map<UUID, State> states = new HashMap<>();
    private static final class State {
        long lastCast;
        long nextRegen;
        Spell selected = Spell.FIREBALL;
        final Map<Spell, Long> ready = new EnumMap<>(Spell.class);
    }
    private MageMana() {}
    public static int maximum(int level) { return 100 + 2 * (PlayerStats.growthLevel(level) - 1); }
    public static void prepare(SkillsMCPlayer p) {
        if (p.getClassType() != ClassType.MAGE) return;
        int maximum = maximum(p.getLevel());
        // Legacy profiles had an unused pool of 10. Migrate once, then preserve current mana.
        if (p.getMaxMana() < 100) p.setMana(maximum);
        p.setMaxMana(maximum);
        p.setMana(Math.clamp(p.getMana(), 0, maximum));
    }
    public static void joined(UUID id, long now) {
        State s = states.computeIfAbsent(id, ignored -> new State());
        s.nextRegen = now + 1000;
        s.lastCast = Math.max(s.lastCast, now); // No offline regeneration or reconnect delay bypass.
    }
    public static Spell selected(UUID id) { return states.computeIfAbsent(id, ignored -> new State()).selected; }
    public static void cycle(UUID id) {
        State s = states.computeIfAbsent(id, ignored -> new State());
        s.selected = s.selected == Spell.FIREBALL ? Spell.ERUPTION : Spell.FIREBALL;
    }
    public static String cast(UUID id, SkillsMCPlayer p, Spell spell, long now, Runnable action) {
        if (p == null || p.getClassType() != ClassType.MAGE) return "Mage class required";
        prepare(p);
        State s = states.computeIfAbsent(id, ignored -> new State());
        if (now < s.ready.getOrDefault(spell, 0L)) return "Spell cooling down";
        if (p.getMana() < spell.cost) return "Not enough mana";
        int before = p.getMana();
        p.setMana(before - spell.cost);
        try { action.run(); }
        catch (RuntimeException ex) { p.setMana(before); throw ex; }
        s.ready.put(spell, now + spell.cooldown);
        s.lastCast = now;
        s.nextRegen = now + 3000;
        return null;
    }
    public static void tick(UUID id, SkillsMCPlayer p, long now) {
        if (p.getClassType() != ClassType.MAGE) return;
        prepare(p);
        State s = states.computeIfAbsent(id, ignored -> new State());
        if (now - s.lastCast >= 3000 && now >= s.nextRegen) {
            p.setMana(Math.min(p.getMaxMana(), p.getMana() + 5));
            s.nextRegen = now + 1000;
        }
    }
    public static String display(UUID id, SkillsMCPlayer p) {
        return "Mana " + p.getMana() + "/" + p.getMaxMana() + " • "
                + (selected(id) == Spell.FIREBALL ? "Fireball" : "Lava Eruption");
    }
    public static void clear() { states.clear(); }
}
