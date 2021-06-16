package ragnaorok.Main.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ragnaorok.Main.Main;

import static org.bukkit.Sound.ENTITY_GENERIC_EXPLODE;

public class KaboomCommand implements CommandExecutor {

    public Main plugin;

    public KaboomCommand(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("souls").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("kaboom")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You cannot run this command!");
                return true;
            }
            Player player = (Player) sender;
            explode(player);
            return true;
        }
        return false;
    }
    public void explode(Player player) {
        World world = player.getWorld();
        Location loc = player.getLocation();
        Location particleLoc = loc.clone();
        for (double t = 0; t <= Math.PI; t += Math.PI / 10) { //Sphere
            double radius = Math.sin(t);
            double y = Math.cos(t);
            for (double i = 0; i < Math.PI * 2; i += Math.PI / 10) {
                double x = Math.cos(i) * radius;
                double z = Math.sin(i) * radius;
                loc.add(x, y, z);
                world.spawnParticle(Particle.SMOKE_LARGE, particleLoc, 10);
                world.spawnParticle(Particle.EXPLOSION_HUGE, particleLoc, 10);
                world.spawnParticle(Particle.EXPLOSION_LARGE, particleLoc, 1);
                world.spawnParticle(Particle.EXPLOSION_NORMAL, particleLoc, 1);
                world.spawnParticle(Particle.FLASH, particleLoc, 10);
                loc.subtract(x, y, z);
            }
        }
        world.playSound(particleLoc, ENTITY_GENERIC_EXPLODE,73,1);
    }
}
