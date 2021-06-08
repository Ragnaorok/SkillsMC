package ragnaorok.Main;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new SkillSelectCommand(this), this);
        this.getCommand("skill").setExecutor(new SkillSelectCommand(this));
        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new LevelUpListener(), this);
        getServer().getPluginManager().registerEvents(new DoubleJump(), this);

    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

