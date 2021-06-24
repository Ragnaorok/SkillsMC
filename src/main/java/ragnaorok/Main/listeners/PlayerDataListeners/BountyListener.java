package ragnaorok.Main.listeners.PlayerDataListeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import ragnaorok.Main.managers.BountyManager;
import ragnaorok.Main.managers.SoulsManager;


public class BountyListener implements Listener {
    @EventHandler
    public void bountyListener(PlayerDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player victim = event.getEntity();
            if (victim.getKiller() instanceof Player) {
                Player killer = victim.getKiller();
                if (BountyManager.getPlayerBounty(victim) == 0) {
                    BountyManager.addBountyToPlayer(killer, 1);
                    Bukkit.broadcastMessage(ChatColor.RED + killer.getName() + "'s bounty has increased");
                } else if (BountyManager.getPlayerBounty(victim) > 0) {
                    SoulsManager.removePlayerCurrency(victim, BountyManager.getPlayerBounty(victim));
                    SoulsManager.addCurrencyToPlayer(killer, BountyManager.getPlayerBounty(victim));
                    BountyManager.setPlayerBounty(victim, 0);
                    Bukkit.broadcastMessage(ChatColor.GREEN + killer.getName() + " has killed " + victim.getName() + " and obtained their bounty");
                }
            }
        }
    }
}
