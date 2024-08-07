package ragnaorok.Main.listeners.PlayerDataListeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import ragnaorok.Main.Constant;
import ragnaorok.Main.SkillsMCPlayer;
import ragnaorok.Main.ActionBarUtil;

public class ExperienceListener implements Listener {

    @EventHandler
    public void experienceHandler(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player) {
            Player player = event.getEntity().getKiller();
            SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString());
            int xpGained = getExperienceForEntity(event.getEntityType());
            smPlayer.addExperience(xpGained);
            updateActionBar(player, smPlayer);
        }
    }

    private int getExperienceForEntity(EntityType entityType) {
        switch (entityType) {
            // E Rank monster(s)
            case SLIME, MAGMA_CUBE, ENDERMITE, SILVERFISH:
                return getRandomExperience(1, 2);

            // D Rank monster(s)
            case SPIDER, CAVE_SPIDER:
                return getRandomExperience(2, 4);
            case ZOMBIE, ZOMBIE_VILLAGER, HUSK, DROWNED, PHANTOM, ZOMBIFIED_PIGLIN:
                return getRandomExperience(3, 5);
            case SKELETON, STRAY:
                return getRandomExperience(4, 6);

            // C Rank monster(s)
            case CREEPER, ENDERMAN:
                return getRandomExperience(5, 7);
            case WITHER_SKELETON, PILLAGER, PIGLIN:
                return getRandomExperience(6, 8);
            case WITCH:
                return getRandomExperience(7, 9);

            // B Rank monster(s)
            case VINDICATOR:
                return getRandomExperience(8, 10);
            case ILLUSIONER, BLAZE:
                return getRandomExperience(10, 12);
            case PIGLIN_BRUTE, GHAST:
                return getRandomExperience(12, 14);
            case SHULKER:
                return getRandomExperience(13, 15);

            // A Rank monster(s)
            case EVOKER:
                return getRandomExperience(15, 20);
            case RAVAGER:
                return getRandomExperience(20, 25);

            // S Rank monster(s)
            case ELDER_GUARDIAN:
                return getRandomExperience(100, 150);

            // SS Rank monster(s)
            case ENDER_DRAGON:
                return getRandomExperience(300, 400);

            // SSS Rank monster(s)
            case WITHER:
                return getRandomExperience(450, 550);
            case WARDEN:
                return getRandomExperience(700, 800);
        }
        return 0;
    }

    private int getRandomExperience(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    private void updateActionBar(Player player, SkillsMCPlayer smPlayer) {
        String message = "Level " + smPlayer.getLevel() + " - XP: " + smPlayer.getExperience() + "/" + smPlayer.getExpToNextLevel();
        ActionBarUtil.sendActionBar(player, message);
    }
}
