package ragnaorok.Main;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ragnaorok.Main.commands.*;
import ragnaorok.Main.customEnchants.ReinforceEnchant;
import ragnaorok.Main.listeners.*;
import ragnaorok.Main.listeners.ToolListeners.AxeListener;
import ragnaorok.Main.listeners.movementListeners.CrouchJumpListener;
import ragnaorok.Main.listeners.playerDataListeners.LevelUpListener;
import ragnaorok.Main.listeners.ToolListeners.*;
import ragnaorok.Main.managers.BountyManager;
import ragnaorok.Main.managers.ManaManager;
import ragnaorok.Main.managers.SoulsManager;

import java.util.Objects;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            System.out.println("Attempting to load Player Data...");
            SoulsManager.loadSoulsFile();
            BountyManager.loadBountyFile();
            ManaManager.loadManaFile();
            ClassType.loadPlayerClassTypeFile();
            System.out.println("Successfully loaded Player Data");
        } catch (Exception e) {
            e.printStackTrace();
        }

        PluginManager pm = this.getServer().getPluginManager();
        Objects.requireNonNull(this.getCommand("souls")).setExecutor(new SoulsCommand(this));
        Objects.requireNonNull(this.getCommand("kaboom")).setExecutor(new KaboomCommand(this));
        Objects.requireNonNull(this.getCommand("saveAll")).setExecutor(new SaveAllCommand(this));
        Objects.requireNonNull(this.getCommand("reinforce")).setExecutor(new ReinforceEnchant(this));
        Objects.requireNonNull(this.getCommand("check")).setExecutor(new EnchantCheck(this));
        Objects.requireNonNull(this.getCommand("class")).setExecutor(new ClassCommand(this));
        Objects.requireNonNull(this.getCommand("profile")).setExecutor(new ProfileCommand(this));
        pm.registerEvents(new ClassCommand(this), this);
        getServer().getPluginManager().registerEvents(new ClassCommand(), this);
        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new LevelUpListener(), this);
        getServer().getPluginManager().registerEvents(new CrouchJumpListener(), this);
        getServer().getPluginManager().registerEvents(new NetheriteHoeListener(), this);
        getServer().getPluginManager().registerEvents(new BlazeRodListener(), this);
        getServer().getPluginManager().registerEvents(new BowListener(), this);
        getServer().getPluginManager().registerEvents(new CrossBowListener(), this);
        getServer().getPluginManager().registerEvents(new ShieldListener(), this);
        getServer().getPluginManager().registerEvents(new AxeListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathInventory(this), this);
        getServer().getPluginManager().registerEvents(new LoginListener(), this);
        System.out.println("Plugin Enabled");
    }

    @Override
    public void onDisable() {
        try {
            SoulsManager.saveSoulsFile();
            BountyManager.saveBountyFile();
            ManaManager.saveManaFile();
            ClassType.savePlayerClassTypeFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}