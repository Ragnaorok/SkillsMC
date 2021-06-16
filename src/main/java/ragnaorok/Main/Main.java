package ragnaorok.Main;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ragnaorok.Main.commands.KaboomCommand;
import ragnaorok.Main.commands.SaveAllCommand;
import ragnaorok.Main.commands.SoulsCommand;
import ragnaorok.Main.listeners.*;
import ragnaorok.Main.managers.BountyManager;
import ragnaorok.Main.managers.CurrencyManager;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            CurrencyManager.loadCurrencyFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            BountyManager.loadBountyFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PluginManager pm = this.getServer().getPluginManager();
        //pm.registerEvents(new SkillSelectCommand(this), this);
        //this.getCommand("skill").setExecutor(new SkillSelectCommand(this));
        this.getCommand("souls").setExecutor(new SoulsCommand(this));
        this.getCommand("kaboom").setExecutor(new KaboomCommand(this));
        this.getCommand("saveAll").setExecutor(new SaveAllCommand(this));
        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new LevelUpListener(), this);
        getServer().getPluginManager().registerEvents(new CrouchJumpListener(), this);
        getServer().getPluginManager().registerEvents(new BountyListener(), this);
        getServer().getPluginManager().registerEvents(new BlockingListener(), this);
        System.out.println("Plugin Enabled");
    }

    @Override
    public void onDisable() {
    }
}

