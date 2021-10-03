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
import ragnaorok.Main.Constant;
import ragnaorok.Main.SkillsMCPlayer;

import java.util.Objects;

import static org.bukkit.Sound.ENTITY_BLAZE_BURN;
import static org.bukkit.Sound.ENTITY_BLAZE_SHOOT;
import static org.bukkit.entity.EntityType.FIREBALL;

public class PlayerClassSkills {
    private JavaPlugin plugin;

    public PlayerClassSkills(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static void displayManaBar(Player player) {
        String uuid = player.getUniqueId().toString();
        SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new ComponentBuilder(smPlayer.getMana() + "/10").color(ChatColor.DARK_BLUE).create());
    }

    public static void useLeftSkill(Player player) {
        String uuid = player.getUniqueId().toString();
        SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
        World world = player.getWorld();
        Location origin = player.getEyeLocation();
        Vector direction = origin.getDirection();
        Location destination = origin.clone().add(direction);
        Material mainHand = player.getInventory().getItemInMainHand().getType();

        switch (smPlayer.getClassType()) {
            case MAGE:
                if (useMana(player, 1)) {
                    if (mainHand == Material.BLAZE_ROD) {
                        player.sendMessage(org.bukkit.ChatColor.GREEN + "Blaze Rod Skill: Fireball"); // Fireball Skill
                        direction.normalize();
                        direction.multiply(3);
                        Location loc = destination.add(direction);
                        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.LAVA, loc, 10);
                        world.spawnEntity(loc, FIREBALL);
                        world.playSound(loc, ENTITY_BLAZE_SHOOT, 20, 1);
                        world.playSound(loc, ENTITY_BLAZE_BURN, 20, 1);
                    }
                    displayManaBar(player);
                }
                return;
            case WARRIOR:
                if (useMana(player, 5)) {

                }
                return;
            case CLERIC:
                if (useMana(player, 4)) {

                }
                return;
            case ARCHER:
                if (useMana(player, 3)) {


                }
                return;
        }
    }

    public static void useRightSkill(Player player) {
        Material mainHand = player.getInventory().getItemInMainHand().getType();
        String uuid = player.getUniqueId().toString();
        SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
        if (smPlayer.getClassType() == null)
            return;

        switch (smPlayer.getClassType()) {
            case MAGE:
                if (mainHand == Material.BLAZE_ROD) {
                    if (smPlayer.getMana() <= 10) {
                        smPlayer.setMana(smPlayer.getMana() + 1);
                    } else {
                        player.sendMessage("You have full mana");
                    }
                    displayManaBar(player);
                }
                return;
            case ARCHER:
            case CLERIC:
            case WARRIOR:
        }
    }

    public static boolean useMana(Player player, int val) {
        String uuid = player.getUniqueId().toString();
        SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
        boolean cast = false;
        if (smPlayer.getMana() >= val) {
            smPlayer.setMana(smPlayer.getMana() - val);
            cast = true;
        }
        return cast;
    }

}

