package ragnaorok.Main.listeners.ToolListeners;

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
import org.bukkit.util.Vector;
import ragnaorok.Main.ClassType;
import ragnaorok.Main.Constant;
import ragnaorok.Main.SkillsMCPlayer;
import ragnaorok.Main.Main;

import java.util.HashMap;
import java.util.Map;

public class BowListener implements Listener {
    private final Main plugin;
    private final Map<String, Long> leftCooldown = new HashMap<>();
    private final Map<String, Long> shiftCooldown = new HashMap<>();
    private final Map<String, Long> duration = new HashMap<>();

    public BowListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Material type = player.getItemInHand().getType();

        if (type != Material.BOW) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
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

        leftCooldown.put(player.getName(), System.currentTimeMillis() + (10 * 1000));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 25, 3));
    }

    private void activateTrueshot(Player player) {
        if (isCooldownActive(player, shiftCooldown)) {
            long time = getCooldownTimeLeft(player, shiftCooldown);
            player.sendMessage(ChatColor.DARK_GRAY + "" + time + " second(s)");
            return;
        }

        shiftCooldown.put(player.getName(), System.currentTimeMillis() + (30 * 1000));
        duration.put(player.getName(), System.currentTimeMillis() + (4 * 1000));
        player.sendMessage(ChatColor.GREEN + "Trueshot activated");
    }

    private boolean isCooldownActive(Player player, Map<String, Long> cooldownMap) {
        return cooldownMap.containsKey(player.getName()) && cooldownMap.get(player.getName()) > System.currentTimeMillis();
    }

    private long getCooldownTimeLeft(Player player, Map<String, Long> cooldownMap) {
        return (cooldownMap.get(player.getName()) - System.currentTimeMillis()) / 1000;
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString());

            if (smPlayer.getClassType() == ClassType.ARCHER && isTrueshotActive(player)) {
                enhanceArrow(event.getProjectile(), player);
            }
        }
    }

    private boolean isTrueshotActive(Player player) {
        return duration.getOrDefault(player.getName(), 0L) > System.currentTimeMillis();
    }

    private void enhanceArrow(Entity projectile, Player player) {
        if (projectile instanceof Arrow) {
            Arrow arrow = (Arrow) projectile;
            arrow.setVelocity(player.getEyeLocation().getDirection().multiply(4));
            arrow.setCritical(true);
            createArrowTrail(arrow);
        }
    }

    private void createArrowTrail(Arrow arrow) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (arrow.isDead() || !arrow.isValid() || arrow.isOnGround()) {
                    this.cancel();
                    return;
                }
                Location arrowLocation = arrow.getLocation();
                arrow.getWorld().spawnParticle(Particle.GLOW, arrowLocation, 10);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
