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
import ragnaorok.Main.SkillsMCPlayer;

import static org.bukkit.Sound.ENTITY_BLAZE_BURN;
import static org.bukkit.Sound.ENTITY_BLAZE_SHOOT;
import static org.bukkit.entity.EntityType.FIREBALL;
import static ragnaorok.Main.managers.ManaManager.removePlayerMana;

public class PlayerClassManager {
    private static ClassType classType;
    private JavaPlugin plugin;

    public PlayerClassManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static void displayManaBar(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new ComponentBuilder(getMana + "/10").color(ChatColor.DARK_BLUE).create());
    }

    public static void useAbility(Player player) {
        if (SkillsMCPlayer.getClass(player) == null) return;
        switch (SkillsMCPlayer.getClass(player)) {
            case WARRIOR:
                if (useMana(player,1) == true) {
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
                }
                return;
            case MAGE:
                if (useMana(player,5) == true) {
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
                }
                return;
            case CLERIC:
                useMana(player,4);


            case ARCHER:
                useMana(player,3);
                //ability
        }
    }

    public static boolean useMana(Player player, int val) {
        boolean cancel = false;
        if (getMana(player) >= val) {
            removePlayerMana(player, val);
            cancel = true;
        }
      return cancel;
    }

    public static ClassType getClassType() {
        return classType;
    }

}

