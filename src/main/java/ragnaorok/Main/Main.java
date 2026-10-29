package ragnaorok.Main;

import de.slikey.effectlib.EffectManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import ragnaorok.Main.commands.*;
import ragnaorok.Main.listeners.PlayerDataListeners.ExperienceListener;
import ragnaorok.Main.listeners.*;
import ragnaorok.Main.listeners.ToolListeners.AxeListener;
import ragnaorok.Main.listeners.ToolListeners.*;
import java.util.Objects;

public final class Main extends JavaPlugin implements @NotNull Listener {
    private static final long AUTOSAVE_INTERVAL_TICKS = 10L * 60L * 20L;
    private EffectManager effectManager;
    private ragnaorok.Main.data.PlayerStore playerStore;
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        playerStore = new ragnaorok.Main.data.PlayerStore(java.nio.file.Path.of("."), getLogger()::warning);
        effectManager = new EffectManager(this);
        PluginManager pm = this.getServer().getPluginManager();
        Objects.requireNonNull(this.getCommand("souls")).setExecutor(new SoulsCommand(this));
        Objects.requireNonNull(this.getCommand("setLevel")).setExecutor(new SetLevelCommand(this));
        Objects.requireNonNull(this.getCommand("kaboom")).setExecutor(new KaboomCommand(this));
        ClassCommand classCommand = new ClassCommand(this);
        Objects.requireNonNull(this.getCommand("class")).setExecutor(classCommand);
        Objects.requireNonNull(this.getCommand("profile")).setExecutor(new ProfileCommand(this));
        Objects.requireNonNull(this.getCommand("guide")).setExecutor(new Guidebook(this));

        //Objects.requireNonNull(this.getCommand("reinforce")).setExecutor(new ReinforceEnchant(this));
        //Objects.requireNonNull(this.getCommand("check")).setExecutor(new EnchantCheck(this));

        pm.registerEvents(new Guidebook(this), this);
        pm.registerEvents(classCommand, this);
        pm.registerEvents(new ragnaorok.Main.skills.SkillAccess(), this);
        pm.registerEvents(new ragnaorok.Main.progression.SkillScaling(), this);
        getServer().getPluginManager().registerEvents(new ragnaorok.Main.progression.EliteMobs(), this);
        var mobLevels = new ragnaorok.Main.progression.MobLevels();
        pm.registerEvents(mobLevels, this);
        for (Player player : Bukkit.getOnlinePlayers()) mobLevels.activate(player);
        pm.registerEvents(new ProfileCommand(this), this);

        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new LoginListener(this), this);
        getServer().getPluginManager().registerEvents(new ExperienceListener(), this);
        getServer().getPluginManager().registerEvents(new CrossBowListener(), this);
        getServer().getPluginManager().registerEvents(new ShieldListener(), this);
        getServer().getPluginManager().registerEvents(new AxeListener(), this);
        getServer().getPluginManager().registerEvents(new CrouchJumpListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathInventory(this), this);
        getServer().getPluginManager().registerEvents(new MageListener(this), this);
        getServer().getPluginManager().registerEvents(new BowListener(this), this);
        getServer().getPluginManager().registerEvents(new SwordListener(this), this);
        getServer().getPluginManager().registerEvents(new ArrowParryListener(), this);
        getServer().getPluginManager().registerEvents(new DualSwordListener(this), this);
        getServer().getPluginManager().registerEvents(new HoeListener(this), this);

        //getServer().getPluginManager().registerEvents(new Test(), this);

        Bukkit.getPluginManager().registerEvents(this, this);

        // Create and register the custom item and recipe
        BlastAxe.createBlastAxe(this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (loadPlayer(player)) ClassPassives.apply(player,
                    Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString()).getClassType());
            else player.kickPlayer("SkillsMC could not load your progress. Check the server log.");
        }
        Bukkit.getScheduler().runTaskTimer(this, this::saveAllPlayers, AUTOSAVE_INTERVAL_TICKS, AUTOSAVE_INTERVAL_TICKS);
        Objects.requireNonNull(getCommand("saveAll")).setExecutor((sender, command, label, args) -> {
            if (!sender.hasPermission("skillsmc.save")) { sender.sendMessage("You cannot save player data."); return true; }
            sender.sendMessage(saveAllPlayers() ? "SkillsMC player data saved." : "Some saves failed. Check the server log.");
            return true;
        });
        getLogger().info("Plugin Enabled");

        // Schedule regular updates for the action bar
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString());
                    if (smPlayer != null) {
                        String message = smPlayer.getLevel() >= ragnaorok.Main.progression.PlayerStats.GROWTH_CAP
                                ? "Level 25 - MAX"
                                : "Level " + smPlayer.getLevel() + " - XP: " + smPlayer.getExperience() + "/" + smPlayer.getExpToNextLevel();
                        if (smPlayer.getClassType() == ClassType.MAGE) {
                            if (!player.isDead()) ragnaorok.Main.progression.MageMana.tick(player.getUniqueId(), smPlayer, System.currentTimeMillis());
                            message += " • " + ragnaorok.Main.progression.MageMana.display(player.getUniqueId(), smPlayer);
                        }
                        ActionBarUtil.sendActionBar(player, message);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L); // Update every second
    }

    @Override
    public @NotNull Logger getSLF4JLogger() {
        return super.getSLF4JLogger();
    }

    @Override
    public void onDisable() {
        ragnaorok.Main.skills.SkillAccess.shutdown();
        ragnaorok.Main.progression.MageMana.clear();
        saveAllPlayers();
        if (effectManager != null) effectManager.dispose();
        HandlerList.unregisterAll((Listener) this);
    }

    public boolean loadPlayer(Player player) {
        String id = player.getUniqueId().toString();
        SkillsMCPlayer retained = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(id);
        if (retained != null) { retained.setPlayer(player); ragnaorok.Main.progression.MageMana.joined(player.getUniqueId(), System.currentTimeMillis()); return true; }
        try {
            SkillsMCPlayer profile = playerStore.load(player.getUniqueId());
            if (profile == null) profile = new SkillsMCPlayer(player, 10, 10, 0, 0, 0, ClassType.NONE);
            profile.setPlayer(player);
            ragnaorok.Main.progression.MageMana.prepare(profile);
            ragnaorok.Main.progression.MageMana.joined(player.getUniqueId(), System.currentTimeMillis());
            Constant.SKILLS_MC_PLAYER_HASH_MAP.put(id, profile);
            return true;
        } catch (java.io.IOException e) {
            getLogger().log(java.util.logging.Level.SEVERE, "Cannot load progress for " + id + "; existing files preserved", e);
            return false;
        }
    }

    public boolean savePlayer(java.util.UUID id, SkillsMCPlayer profile) {
        try {
            playerStore.save(id, profile);
            return true;
        } catch (java.io.IOException e) {
            getLogger().log(java.util.logging.Level.SEVERE, "Cannot save progress for " + id + "; retrying at next save", e);
            return false;
        }
    }

    private boolean saveAllPlayers() {
        if (playerStore == null) return true;
        boolean success = true;
        var iterator = Constant.SKILLS_MC_PLAYER_HASH_MAP.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            boolean saved = savePlayer(java.util.UUID.fromString(entry.getKey()), entry.getValue());
            success &= saved;
            if (saved && entry.getValue().getPlayer() == null) iterator.remove();
        }
        return success;
    }

    public static Main getInstance() {
        return instance;
    }
}
