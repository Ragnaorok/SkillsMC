package ragnaorok.Main.listeners.ToolListeners;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.*;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;
import ragnaorok.Main.skills.SkillAccess;

/** One short ray trace per eligible swing; no polling task. */
public final class ArrowParryListener implements Listener {
    private final Map<UUID, Long> ready = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void swing(PlayerArmSwingEvent event) {
        var player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND
                || !SkillAccess.allows(player, SkillAccess.WeaponSkill.SWORD)
                || !Tag.ITEMS_SWORDS.isTagged(player.getInventory().getItemInMainHand().getType())
                || !player.getInventory().getItemInOffHand().getType().isAir()) return;
        long now = System.nanoTime();
        ready.entrySet().removeIf(e -> now >= e.getValue());
        if (ready.containsKey(player.getUniqueId())) return;
        var eye = player.getEyeLocation();
        Vector direction = eye.getDirection();
        var wall = player.getWorld().rayTraceBlocks(eye, direction, 4, FluidCollisionMode.NEVER, true);
        double reach = wall == null ? 4 : wall.getHitPosition().distance(eye.toVector());
        if (reach <= 0) return;
        var hit = player.getWorld().rayTraceEntities(eye, direction, reach, .5,
                entity -> entity instanceof AbstractArrow arrow && !arrow.isInBlock()
                        && arrow.isValid() && !arrow.isDead() && arrow.getShooter() != player
                        && incoming(arrow.getVelocity(), eye.toVector().subtract(arrow.getLocation().toVector())));
        if (hit == null || !(hit.getHitEntity() instanceof AbstractArrow arrow)) return;
        arrow.setShooter(player);
        // Reflected shots no longer carry the original mob's level multiplier.
        arrow.getPersistentDataContainer().remove(new NamespacedKey("skillsmc", "mob_projectile_level"));
        arrow.setVelocity(direction.multiply(2));
        ready.put(player.getUniqueId(), now + 500_000_000L);
        player.getWorld().playSound(arrow.getLocation(), Sound.ITEM_SHIELD_BLOCK, .6f, 1.5f);
        player.getWorld().spawnParticle(Particle.CRIT, arrow.getLocation(), 6, .1, .1, .1, .05);
    }

    static boolean incoming(Vector velocity, Vector towardPlayer) {
        return velocity.lengthSquared() > .0001 && velocity.dot(towardPlayer) > 0;
    }
}
