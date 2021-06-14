package ragnaorok.Main;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ragnaorok.Main.commands.SkillSelectCommand;
import ragnaorok.Main.commands.SoulsCommand;
import ragnaorok.Main.managers.BountyManager;
import ragnaorok.Main.managers.CurrencyManager;

import java.io.IOException;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        CurrencyManager currencyManager = new CurrencyManager(this);
        try {
            currencyManager.loadCurrencyFile();
        } catch (Exception e){
            e.printStackTrace();
        }
        BountyManager bountyManager = new BountyManager(this);
        try {
            bountyManager.loadCurrencyFile();
        } catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
        }
        PluginManager pm = this.getServer().getPluginManager();
        //pm.registerEvents(new SkillSelectCommand(this), this);
        //this.getCommand("skill").setExecutor(new SkillSelectCommand(this));
        this.getCommand("souls").setExecutor(new SoulsCommand(this));
        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new LevelUpListener(), this);
        getServer().getPluginManager().registerEvents(new CrouchJump(), this);
        getServer().getPluginManager().registerEvents(new BountyListener(), this);
        registerManagers();

    }
    @Override
    public void onDisable() {
        CurrencyManager currencyManager = new CurrencyManager(this);
        try {
            currencyManager.saveCurrencyFile();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    public void registerManagers(){
        new BountyManager(this);
        new CurrencyManager(this);
    }
}

