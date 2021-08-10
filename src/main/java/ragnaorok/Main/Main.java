package ragnaorok.Main;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ragnaorok.Main.commands.*;
import ragnaorok.Main.customEnchants.ReinforceEnchant;
import ragnaorok.Main.listeners.*;
import ragnaorok.Main.listeners.MovementListeners.CrouchJumpListener;
import ragnaorok.Main.listeners.PlayerDataListeners.BountyListener;
import ragnaorok.Main.listeners.PlayerDataListeners.LevelUpListener;
import ragnaorok.Main.listeners.ToolListeners.*;
import ragnaorok.Main.managers.BountyManager;
import ragnaorok.Main.managers.SoulsManager;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            System.out.println("Attempting to load Currency Stats...");
            SoulsManager.loadSoulsFile();
            System.out.println("Successfully loaded Currency Stats");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("Attempting to load Bounty Stats...");
            BountyManager.loadBountyFile();
            System.out.println("Successfully loaded Bounty Stats");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("Attempting to load PlayerClass Data...");
            ClassType.loadPlayerClassTypeFile();
            System.out.println("Successfully loaded PlayerClass Data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        PluginManager pm = this.getServer().getPluginManager();
        this.getCommand("souls").setExecutor(new SoulsCommand(this));
        this.getCommand("kaboom").setExecutor(new KaboomCommand(this));
        this.getCommand("saveAll").setExecutor(new SaveAllCommand(this));
        this.getCommand("reinforce").setExecutor(new ReinforceEnchant(this));
        this.getCommand("check").setExecutor(new enchantCheck(this));
        this.getCommand("class").setExecutor(new ClassCommand(this));
        this.getCommand("profile").setExecutor(new ProfileCommand(this));
        pm.registerEvents(new ClassCommand(this), this);
        getServer().getPluginManager().registerEvents(new ClassCommand(), this);
        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new LevelUpListener(), this);
        getServer().getPluginManager().registerEvents(new CrouchJumpListener(), this);
        getServer().getPluginManager().registerEvents(new BountyListener(), this);
        getServer().getPluginManager().registerEvents(new ShieldListener(), this);
        getServer().getPluginManager().registerEvents(new CrossBowSkill(), this);
        getServer().getPluginManager().registerEvents(new BlazeRodListener(), this);
        getServer().getPluginManager().registerEvents(new BountyListener(), this);
        getServer().getPluginManager().registerEvents(new BowListener(), this);
        getServer().getPluginManager().registerEvents(new NetheriteHoeListener(), this);
        getServer().getPluginManager().registerEvents(new AxeListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathInventory(this), this);
        getServer().getPluginManager().registerEvents(new loginMessage(), this);
        System.out.println("Plugin Enabled");
    }

    @Override
    public void onDisable() {
        try {
            SoulsManager.saveSoulsFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            BountyManager.saveBountyFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ClassType.savePlayerClassTypeFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}