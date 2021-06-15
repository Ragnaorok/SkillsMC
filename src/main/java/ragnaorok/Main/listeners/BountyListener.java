package ragnaorok.Main.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import ragnaorok.Main.Main;
import ragnaorok.Main.managers.BountyManager;
import ragnaorok.Main.managers.CurrencyManager;


public class BountyListener implements Listener {
    public Main plugin;
    @EventHandler
    public void bountyListener(PlayerDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player victim = event.getEntity();
            if (victim.getKiller() instanceof Player) {
                BountyManager bmanager = new BountyManager(plugin);
                CurrencyManager smanager = new CurrencyManager(plugin);
                Player killer = victim.getKiller();
                if (bmanager.getPlayerBounty(victim) == 0) {
                    bmanager.addBountyToPlayer(killer, +1);
                    Bukkit.broadcastMessage(killer.getName() + "'s bounty has increased");
                } else if (bmanager.getPlayerBounty(victim) > 0) {
                    smanager.removePlayerCurrency(victim, bmanager.getPlayerBounty(victim));
                    smanager.addCurrencyToPlayer(killer, bmanager.getPlayerBounty(victim));
                    bmanager.setPlayerBounty(victim, 0);
                    Bukkit.broadcastMessage(killer.getName() + " has killed " + victim.getName() + " and obtained their bounty");
                }
            }
        }
    }
}
