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
    private EffectManager effectManager;
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        effectManager = new EffectManager(this);
        PluginManager pm = this.getServer().getPluginManager();
        Objects.requireNonNull(this.getCommand("souls")).setExecutor(new SoulsCommand(this));
        Objects.requireNonNull(this.getCommand("setLevel")).setExecutor(new SetLevelCommand(this));
        Objects.requireNonNull(this.getCommand("kaboom")).setExecutor(new KaboomCommand(this));
        Objects.requireNonNull(this.getCommand("class")).setExecutor(new ClassCommand(this));
        Objects.requireNonNull(this.getCommand("profile")).setExecutor(new ProfileCommand(this));
        Objects.requireNonNull(this.getCommand("guide")).setExecutor(new Guidebook(this));

        //Objects.requireNonNull(this.getCommand("reinforce")).setExecutor(new ReinforceEnchant(this));
        //Objects.requireNonNull(this.getCommand("check")).setExecutor(new EnchantCheck(this));

        pm.registerEvents(new Guidebook(this), this);
        pm.registerEvents(new ClassCommand(this), this);
        pm.registerEvents(new ProfileCommand(this), this);

        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new LoginListener(), this);
        getServer().getPluginManager().registerEvents(new ExperienceListener(), this);
        getServer().getPluginManager().registerEvents(new CrossBowListener(), this);
        getServer().getPluginManager().registerEvents(new ShieldListener(), this);
        getServer().getPluginManager().registerEvents(new AxeListener(), this);
        getServer().getPluginManager().registerEvents(new CrouchJumpListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathInventory(this), this);
        getServer().getPluginManager().registerEvents(new BlazeRodListener(this), this);
        getServer().getPluginManager().registerEvents(new BowListener(this), this);
        getServer().getPluginManager().registerEvents(new SwordListener(this), this);
        getServer().getPluginManager().registerEvents(new DualSwordListener(this), this);
        getServer().getPluginManager().registerEvents(new HoeListener(this), this);

        //getServer().getPluginManager().registerEvents(new Test(), this);

        Bukkit.getPluginManager().registerEvents(this, this);

        // Create and register the custom item and recipe
        BlastAxe.createBlastAxe(this);

        System.out.println("Plugin Enabled");

        // Schedule regular updates for the action bar
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(player.getUniqueId().toString());
                    if (smPlayer != null) {
                        String message = "Level " + smPlayer.getLevel() + " - XP: " + smPlayer.getExperience() + "/" + smPlayer.getExpToNextLevel();
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
        effectManager.dispose();
        HandlerList.unregisterAll((Listener) this);
    }

    public static Main getInstance() {
        return instance;
    }
}
