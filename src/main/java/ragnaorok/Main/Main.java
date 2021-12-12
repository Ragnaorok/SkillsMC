package ragnaorok.Main;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ragnaorok.Main.commands.*;
import ragnaorok.Main.customEnchants.ReinforceEnchant;
import ragnaorok.Main.listeners.*;
import ragnaorok.Main.listeners.PlayerDataListeners.LevelUpListener;
import ragnaorok.Main.listeners.ToolListeners.AxeListener;
import ragnaorok.Main.listeners.movementListeners.CrouchJumpListener;
import ragnaorok.Main.listeners.ToolListeners.*;

import java.util.Objects;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {

        PluginManager pm = this.getServer().getPluginManager();
        Objects.requireNonNull(this.getCommand("souls")).setExecutor(new SoulsCommand(this));
        Objects.requireNonNull(this.getCommand("kaboom")).setExecutor(new KaboomCommand(this));
        Objects.requireNonNull(this.getCommand("reinforce")).setExecutor(new ReinforceEnchant(this));
        Objects.requireNonNull(this.getCommand("check")).setExecutor(new EnchantCheck(this));
        Objects.requireNonNull(this.getCommand("class")).setExecutor(new ClassCommand(this));
        Objects.requireNonNull(this.getCommand("profile")).setExecutor(new ProfileCommand(this));
        pm.registerEvents(new ClassCommand(this), this);
        pm.registerEvents(new ProfileCommand(this), this);
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
        getServer().getPluginManager().registerEvents(new Test(), this);
        System.out.println("Plugin Enabled");
    }

    @Override
    public void onDisable() {

    }
}