package ragnaorok.Main.listeners.PlayerDataListeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import ragnaorok.Main.Constant;
import ragnaorok.Main.SkillsMCPlayer;

public class BountyListener implements Listener {
    @EventHandler
    public void bountyListener(PlayerDeathEvent event) {

        if (event.getEntity() instanceof Player) {
            Player victim = event.getEntity();
            if (victim.getKiller() instanceof Player) {
                Player killer = victim.getKiller();
                String vuuid = victim.getUniqueId().toString();
                String kuuid = killer.getUniqueId().toString();
                SkillsMCPlayer smVictim = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(vuuid);
                SkillsMCPlayer smKiller = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(kuuid);

                if (smVictim.getBounty() == 0) {
                    smKiller.setBounty(smKiller.getBounty() + 1);
                    Bukkit.broadcastMessage(ChatColor.RED + killer.getName() + "'s bounty has increased");
                } else if (smVictim.getBounty() > 0) {
                    smKiller.setSouls(smKiller.getSouls() + smVictim.getBounty());
                    smVictim.setBounty(0);
                    Bukkit.broadcastMessage(ChatColor.GREEN + killer.getName() + " has killed " + victim.getName() + " and obtained their bounty");
                }
            }
        }
    }
}
