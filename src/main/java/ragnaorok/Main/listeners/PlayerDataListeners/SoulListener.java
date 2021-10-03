package ragnaorok.Main.listeners.PlayerDataListeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ragnaorok.Main.Constant;
import ragnaorok.Main.SkillsMCPlayer;

public class SoulListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Monster) {
            Monster monster = (Monster) event.getEntity();
            if (monster.getKiller() instanceof Player) {
                Player player = monster.getKiller();
                String uuid = player.getUniqueId().toString();
                SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
                if (smPlayer.getSouls() > 99) { //Skill:Blood_Lust
                    if (player.getHealth() < 20) {
                        player.setHealth(player.getHealth() + 1);
                    } else {
                        return;
                    }
                }
                if (smPlayer.getSouls() > 199) { //Skill:Second_Wind
                    if (player.getHealth() <= 10) {
                        player.addPotionEffect((new PotionEffect(PotionEffectType.SPEED, 15, 1)));
                    }
                }
                if (smPlayer.getSouls() > 299) { //Skill:Conqueror
                    if (player.getHealth() >= 15) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 15, 1));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 15, 1));
                    }
                }
            }
        }
    }
}
