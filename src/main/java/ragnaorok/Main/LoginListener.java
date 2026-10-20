package ragnaorok.Main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public final class LoginListener implements Listener {
    private final Main plugin;
    public LoginListener(Main plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;
        if (!plugin.loadPlayer(event.getPlayer())) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                    "SkillsMC could not load your progress. Ask the server owner to check the log.");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SkillsMCPlayer profile = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString());
        if (profile != null) {
            ClassPassives.apply(player, profile.getClassType());
            player.sendMessage("SkillsMC loaded. Use /class to choose your class.");
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            SkillsMCPlayer profile = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString());
            if (player.isOnline() && profile != null) ClassPassives.apply(player, profile.getClassType());
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        String id = event.getPlayer().getUniqueId().toString();
        SkillsMCPlayer profile = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(id);
        if (profile == null) return;
        if (plugin.savePlayer(event.getPlayer().getUniqueId(), profile)) {
            Constant.SKILLS_MC_PLAYER_HASH_MAP.remove(id);
        } else {
            // Retain unsaved progress for retries and reconnects, without retaining the Bukkit Player.
            profile.setPlayer(null);
        }
    }
}
