package ragnaorok.Main;

import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Gui(), this);
        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new LevelUpListener(), this);
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

