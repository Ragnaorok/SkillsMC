package ragnaorok.Main.managers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import ragnaorok.Main.ClassType;

import static org.bukkit.Sound.ENTITY_BLAZE_BURN;
import static org.bukkit.Sound.ENTITY_BLAZE_SHOOT;
import static org.bukkit.entity.EntityType.FIREBALL;

public class PlayerClassManager {
    private static ClassType classType;
    private static int mana;
    private JavaPlugin plugin;

    public PlayerClassManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void ClassManager(Player player) {
        mana = 10;
    }

    public static void addMana() {
        if (getMana() < 10) {
            mana += 1;
        }
    }

    public static void displayManaBar(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new ComponentBuilder(getMana() + "/10").color(ChatColor.DARK_BLUE).create());
    }

    public static void useAbility(Player player) {
        if (ClassType.getPlayerClassType(player) == null) return;
        switch (ClassType.getPlayerClassType(player)) {
            case WARRIOR:
                useMana(1);
                World world = player.getWorld();
                Location origin = player.getEyeLocation();
                Vector direction = origin.getDirection();
                direction.normalize();
                direction.multiply(3);
                Location destination = origin.clone().add(direction);
                Material type = player.getItemInHand().getType();
                if (player.getItemInHand() == null) return;
                if (type == Material.BLAZE_ROD) {
                    player.sendMessage(org.bukkit.ChatColor.GREEN + "Blaze Rod Skill: Fireball");
                    // Fireball Skill
                    Location oloc = destination.add(direction);
                    oloc.getWorld().spawnParticle(Particle.LAVA, oloc, 10);
                    world.spawnEntity(oloc, FIREBALL);
                    world.playSound(oloc, ENTITY_BLAZE_SHOOT, 20, 1);
                    world.playSound(oloc, ENTITY_BLAZE_BURN, 20, 1);
                }
            case MAGE:
                useMana(5);
                //ability
            case CLERIC:
                useMana(4);


            case ARCHER:
                useMana(3);
                //ability
        }
    }

    public static void useMana(int val) {
        if (getMana() >= val) {
            setMana(getMana() - val);
        }
        else{
            System.out.println("Not enough mana");
        }
    }

    public static ClassType getClassType() {
        return classType;
    }

    public static int getMana() {
        return mana;
    }

    public static void setMana(int val) {
        mana = val;
    }

}

