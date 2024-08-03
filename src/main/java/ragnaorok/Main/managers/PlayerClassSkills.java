/*package ragnaorok.Main.managers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import ragnaorok.Main.Constant;
import ragnaorok.Main.SkillsMCPlayer;
import ragnaorok.Main.Tools;

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
        PlayerInventory hand = player.getInventory();
        World world = player.getWorld();
        Location origin = player.getEyeLocation();
        Vector direction = origin.getDirection();
        Location destination = origin.clone().add(direction);
        Material mainHand = player.getInventory().getItemInMainHand().getType();
        switch (smPlayer.getClassType()) {
            case MAGE:
                if (Tools.getLoadOut(hand).equals(Tools.FIRE_WAND)) {
                    if (useMana(player, 1)) {
                        player.sendMessage(org.bukkit.ChatColor.GREEN + "Blaze Rod Skill: Fireball"); // Fireball Skill
                        direction.normalize();
                        direction.multiply(1);
                        Location loc = destination.add(direction);
                        Objects.requireNonNull(loc.getWorld()).spawnParticle(Particle.LAVA, loc, 10);
                        world.spawnEntity(loc, FIREBALL);
                        world.playSound(loc, ENTITY_BLAZE_SHOOT, 20, 1);
                        world.playSound(loc, ENTITY_BLAZE_BURN, 20, 1);

                    }
                }
                return;
            case WARRIOR:
                if (Tools.getLoadOut(hand).equals(Tools.SWORD)){
                    if (useMana(player, 1)) {

                    }

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
            case NONE:
                if (useMana(player, 0)) {
                    if (mainHand == Material.BLAZE_ROD) {

                    }

                }
        }
        displayManaBar(player);
    }

    public static void useRightSkill(Player player) {
        Material mainHand = player.getInventory().getItemInMainHand().getType();
        String uuid = player.getUniqueId().toString();
        SkillsMCPlayer smPlayer = Constant.SKILLS_MC_PLAYER_HASH_MAP.get(uuid);
        PlayerInventory inventory = player.getInventory();
        if (smPlayer.getClassType() == null)
            return;

        switch (smPlayer.getClassType()) {
            case MAGE:
                if (mainHand == Material.BLAZE_ROD) {
                    if (smPlayer.getMana() < 10) {
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
                if (Tools.getMainHand(inventory) == Tools.SWORD && Tools.getOffHand(inventory) == Tools.SWORD) {
                        player.swingOffHand();
                    displayManaBar(player);
                }

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
*/
