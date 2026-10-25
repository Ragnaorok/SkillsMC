package ragnaorok.Main.listeners.ToolListeners;
import ragnaorok.Main.skills.SkillAccess;
import static ragnaorok.Main.skills.SkillAccess.WeaponSkill.BOW;


import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ragnaorok.Main.Main;

import java.util.HashMap;
import java.util.Map;

public class BowListener implements Listener {
    private final Main plugin;
    private final Map<java.util.UUID, Long> leftCooldown = new HashMap<>();
    private final Map<java.util.UUID, Long> shiftCooldown = new HashMap<>();
    private final Map<java.util.UUID, SkillAccess.Session> trueshotReady = new HashMap<>();

    public BowListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) return;
        Material type = player.getItemInHand().getType();

        if (type != Material.BOW) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (!SkillAccess.require(player, BOW)) return;
            handleLeftClick(player);
        }
    }

    private void handleLeftClick(Player player) {
        if (!player.isSneaking()) {
            applySpeedEffect(player);
        } else {
            activateTrueshot(player);
        }
    }

    private void applySpeedEffect(Player player) {
        if (isCooldownActive(player, leftCooldown)) {
            long time = getCooldownTimeLeft(player, leftCooldown);
            player.sendMessage(ChatColor.DARK_GRAY + "Speed will be ready in " + time + " second(s)");
            return;
        }

        leftCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (10 * 1000));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 25, 3));
    }

    private void activateTrueshot(Player player) {
        if (isCooldownActive(player, shiftCooldown)) {
            long time = getCooldownTimeLeft(player, shiftCooldown);
            player.sendMessage(ChatColor.DARK_GRAY + "" + time + " second(s)");
            return;
        }

        shiftCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (10 * 1000));
        var id = player.getUniqueId();
        var prepared = SkillAccess.session(player, BOW);
        trueshotReady.put(id, prepared);
        prepared.onClose(BOW, () -> trueshotReady.remove(id));
        player.sendMessage(ChatColor.GREEN + "Trueshot ready for the next shot");
    }

    private boolean isCooldownActive(Player player, Map<java.util.UUID, Long> cooldownMap) {
        return cooldownMap.containsKey(player.getUniqueId()) && cooldownMap.get(player.getUniqueId()) > System.currentTimeMillis();
    }

    private long getCooldownTimeLeft(Player player, Map<java.util.UUID, Long> cooldownMap) {
        return (cooldownMap.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getBow() == null || event.getBow().getType() != Material.BOW) return;
            var prepared = trueshotReady.remove(player.getUniqueId());
            if (prepared != null && prepared.valid(BOW)) {
                enhanceArrow(event.getProjectile(), player);
                // Prepared state was consumed above.
            }
        }
    }

    private void enhanceArrow(Entity projectile, Player player) {
        if (projectile instanceof Arrow) {
            Arrow arrow = (Arrow) projectile;
            arrow.setVelocity(player.getEyeLocation().getDirection().multiply(8));
            arrow.setCritical(true);
            var session = SkillAccess.session(player, BOW);
            session.track(arrow);
            createArrowTrail(arrow, session);
        }
    }

    private void createArrowTrail(Arrow arrow, SkillAccess.Session session) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!session.valid(BOW) || arrow.isDead() || !arrow.isValid() || arrow.isOnGround()) {
                    this.cancel();
                    return;
                }
                Location arrowLocation = arrow.getLocation();
                arrow.getWorld().spawnParticle(Particle.GLOW, arrowLocation, 15);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
