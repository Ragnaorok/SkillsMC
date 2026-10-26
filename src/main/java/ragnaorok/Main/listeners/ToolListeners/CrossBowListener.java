package ragnaorok.Main.listeners.ToolListeners;
import ragnaorok.Main.skills.SkillAccess;
import ragnaorok.Main.progression.PlayerStats;
import static ragnaorok.Main.skills.SkillAccess.WeaponSkill.CROSSBOW;


import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import ragnaorok.Main.Loadout;

import java.util.HashMap;
import java.util.Map;

public class CrossBowListener implements Listener {
    Map<java.util.UUID, Long> leftCooldown = new HashMap<java.util.UUID, Long>();
    Map<java.util.UUID, Long> shiftCooldown = new HashMap<java.util.UUID, Long>();
    Map<java.util.UUID, Long> duration = new HashMap<java.util.UUID, Long>();

    private final Map<java.util.UUID, SkillAccess.Session> rapidSessions = new HashMap<>();

    @EventHandler
    public void onBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) return;
        Location origin = player.getLocation();
        Vector direction = origin.getDirection();
        PlayerInventory inventory = player.getInventory();

        if (Loadout.getLoadOut(inventory) == Loadout.CROSSBOW) {
            if (!SkillAccess.require(player, CROSSBOW)) return;
            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
                if (!player.isSneaking()) {
                    if (leftCooldown.containsKey(player.getUniqueId())) {
                        if (leftCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                            long time = (leftCooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "Backstep will be ready in " + time + " second(s)");
                            return;
                        }
                    }
                    leftCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (3 * 1000));
                    player.sendMessage(ChatColor.GREEN + "Crossbow Skill: Backstep");
                    direction.multiply(-1);
                    direction.normalize();
                    player.setVelocity(direction);
                }
                if (player.isSneaking()) {
                    if (shiftCooldown.containsKey(player.getUniqueId())) {
                        if (shiftCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                            long time = (shiftCooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "null " + time + " second(s)");
                            return;
                        }
                    }
                }
            }

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (player.isSneaking()) {
                    if (shiftCooldown.containsKey(player.getUniqueId())) {
                        if (shiftCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                            long time = (shiftCooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                            player.sendMessage(ChatColor.DARK_GRAY + "RapidFire will be ready in " + time + " second(s)");
                            return;
                        }
                    }
                    shiftCooldown.put(player.getUniqueId(), System.currentTimeMillis() + (23 * 1000));
                    var id = player.getUniqueId();
                    var prepared = SkillAccess.session(player, CROSSBOW);
                    rapidSessions.put(id, prepared);
                    prepared.onClose(CROSSBOW, () -> { rapidSessions.remove(id); duration.remove(id); });
                    duration.put(player.getUniqueId(), System.currentTimeMillis() + (3 * 1000));
                    player.sendMessage(ChatColor.GREEN + "Rapidfire activated");
                }
                if (!player.isSneaking()) {
                    if (duration.get(player.getUniqueId()) == null)
                        return;
                    if (duration.get(player.getUniqueId()) > System.currentTimeMillis()
                            && rapidSessions.containsKey(player.getUniqueId()) && rapidSessions.get(player.getUniqueId()).valid(CROSSBOW)) {
                        Arrow arrow = player.launchProjectile(Arrow.class);
                        arrow.setDamage(PlayerStats.damage(player, arrow.getDamage()));
                        rapidSessions.get(player.getUniqueId()).track(arrow);
                        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
                    }
                }
            }
        }
    }
}
