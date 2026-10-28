package ragnaorok.Main.skills;

import java.util.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import ragnaorok.Main.*;

/** Main-thread class authorization and lifecycle for prepared/ongoing weapon skills. */
public final class SkillAccess implements Listener {
    public enum WeaponSkill {
        SWORD(ClassType.WARRIOR), DUAL_SWORD(ClassType.WARRIOR), AXE(ClassType.WARRIOR), SHIELD(ClassType.WARRIOR),
        BOW(ClassType.ARCHER), CROSSBOW(ClassType.ARCHER), MAGE_HOE(ClassType.MAGE), HOE(ClassType.CLERIC);
        public final ClassType owner;
        WeaponSkill(ClassType owner) { this.owner = owner; }
        public boolean allows(ClassType actual) { return owner == actual; }
    }
    private static final Map<UUID, Session> sessions = new HashMap<>();
    public static boolean allows(Player player, WeaponSkill skill) {
        SkillsMCPlayer profile = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString());
        return profile != null && skill.allows(profile.getClassType());
    }
    public static boolean require(Player player, WeaponSkill skill) {
        return allows(player, skill);
    }
    public static Session session(Player player, WeaponSkill skill) {
        if (!allows(player, skill)) throw new IllegalStateException("Unauthorized skill");
        return sessions.computeIfAbsent(player.getUniqueId(), id -> new Session(player));
    }
    public static final class Session {
        private final Player player;
        private boolean active = true;
        private final Map<WeaponSkill, Runnable> cleanup = new EnumMap<>(WeaponSkill.class);
        public void onClose(WeaponSkill skill, Runnable action) { cleanup.put(skill, action); }
        private final List<Entity> entities = new ArrayList<>();
        private Session(Player player) { this.player = player; }
        public boolean valid(WeaponSkill skill) {
            return active && player.isOnline() && !player.isDead() && allows(player, skill);
        }
        public void track(Entity entity) {
            entities.removeIf(e -> !e.isValid() || e.isDead());
            entities.add(entity);
        }
        private void close() {
            active = false;
            cleanup.values().forEach(Runnable::run);
            cleanup.clear();
            for (Entity entity : entities) if (entity.isValid()) entity.remove();
            entities.clear();
        }
    }
    public static void invalidate(Player player) {
        Session session = sessions.remove(player.getUniqueId());
        if (session != null) session.close();
    }
    public static void shutdown() {
        sessions.values().forEach(Session::close);
        sessions.clear();
    }
    @EventHandler public void quit(PlayerQuitEvent e) { invalidate(e.getPlayer()); }
    @EventHandler public void death(PlayerDeathEvent e) { invalidate(e.getEntity()); }
    @EventHandler public void world(PlayerChangedWorldEvent e) { invalidate(e.getPlayer()); }
}
