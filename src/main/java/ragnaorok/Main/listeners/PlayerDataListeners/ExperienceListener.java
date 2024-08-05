package ragnaorok.Main.listeners.PlayerDataListeners;

import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import ragnaorok.Main.Constant;
import ragnaorok.Main.SkillsMCPlayer;
import ragnaorok.Main.ActionBarUtil;

public class ExperienceListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player && event.getEntity() instanceof Monster) {
            Player player = event.getEntity().getKiller();
            SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString());
            smPlayer.addExperience(1);
            updateActionBar(player, smPlayer);
        }
    }

    private void updateActionBar(Player player, SkillsMCPlayer smPlayer) {
        String message = "Level " + smPlayer.getLevel() + " - XP: " + smPlayer.getExperience() + "/" + smPlayer.getExpToNextLevel();
        ActionBarUtil.sendActionBar(player, message);
    }
}
