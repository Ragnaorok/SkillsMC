package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import ragnaorok.Main.Tools;


import java.util.HashMap;
import java.util.Map;

import static ragnaorok.Main.managers.PlayerClassSkills.useLeftSkill;
import static ragnaorok.Main.managers.PlayerClassSkills.useRightSkill;

public class Test implements Listener {
    Map<String, Long> duration = new HashMap<String, Long>();
    Map<String, Long> right_Cooldown = new HashMap<String, Long>();

    @EventHandler
    public void onCast(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            useLeftSkill(player);
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            duration.put(player.getName(), System.currentTimeMillis() + (1 * 1000));
            useRightSkill(player);
        }
    }

    @EventHandler
    public void Parry(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Monster && event.getEntity() instanceof Player) {
            Monster monster = (Monster) event.getDamager();
            Player player = (Player) event.getEntity();
            Location loc = player.getLocation();
            Vector direction = loc.getDirection();
            Location mOrigin = monster.getLocation();
            Location cOrigin = mOrigin.clone();
            Vector mDirection = mOrigin.getDirection();
            double damage = event.getDamage();
            PlayerInventory inventory = player.getInventory();
            if (Tools.getMainHand(inventory) == Tools.SWORD && Tools.getOffHand(inventory) == Tools.SWORD) {
                if (duration.get(player.getName()) > System.currentTimeMillis()) {
                    double pDamage = damage / 2;
                    monster.damage(pDamage);
                    mDirection.normalize();
                    monster.setVelocity(mDirection.multiply(-1));
                    event.setCancelled(true);
                    for (int i = 0; i < 3; i++) {
                        Location oloc = cOrigin.add(direction);
                        oloc.getWorld().spawnParticle(Particle.CRIT, oloc, 10);
                    }
                }
            }
        }
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            PotionEffect weak = new PotionEffect(PotionEffectType.WEAKNESS, 2, 1);
            PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 2, 1);
            Player dPlayer = (Player) event.getDamager();
            Player Eplayer = (Player) event.getEntity();
            PlayerInventory inventory = Eplayer.getInventory();
            if (Tools.getMainHand(inventory) == Tools.SWORD && Tools.getOffHand(inventory) == Tools.SWORD) {
                if (duration.get(Eplayer.getName()) > System.currentTimeMillis()) {
                    event.setCancelled(true);
                    dPlayer.addPotionEffect(weak);
                    dPlayer.addPotionEffect(slow);
                }
            }
        }
    }
}



