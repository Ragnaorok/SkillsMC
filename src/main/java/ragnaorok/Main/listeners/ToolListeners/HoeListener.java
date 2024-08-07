package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ragnaorok.Main.Loadout;
import ragnaorok.Main.Main;
import ragnaorok.Main.Particles.EnumShapes;
import ragnaorok.Main.Particles.ParticleShapes;

public class HoeListener implements Listener {

    private final Main plugin;

    public HoeListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHoeUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();

        if (Loadout.getLoadOut(inventory) == Loadout.HOE) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                createHallowedGround(player, event.getClickedBlock());
            } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                if (player.isSneaking()) {
                    castCrescentProjectile(player);
                }
            }
        }
    }

    private void createHallowedGround(Player player, Block clickedBlock) {
        Location location;
        if (clickedBlock != null) {
            location = clickedBlock.getLocation().add(0.5, 1, 0.5);  // Drop slightly above the clicked block
        } else {
            location = player.getLocation();  // Default to player location if no block is clicked
        }
        World world = player.getWorld();

        world.playSound(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
        player.sendMessage(ChatColor.GREEN + "Hallowed Ground created!");

        int interval = 20; // 1 second
        new BukkitRunnable() {
            int counter = 0;
            final int duration = 40; // 2 seconds

            @Override
            public void run() {
                if (counter >= duration) {
                    this.cancel();
                    return;
                }

                ItemStack splashPotion = new ItemStack(Material.SPLASH_POTION);
                PotionMeta potionMeta = (PotionMeta) splashPotion.getItemMeta();
                potionMeta.setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));
                splashPotion.setItemMeta(potionMeta);

                // Spawn the thrown splash potion
                ThrownPotion thrownPotion = (ThrownPotion) world.spawnEntity(location, EntityType.SPLASH_POTION);
                thrownPotion.setItem(splashPotion);
                ParticleShapes.draw(EnumShapes.SPHERE, player.getLocation(), Particle.END_ROD, player);
                counter += interval;
            }
        }.runTaskTimer(plugin, 0L, interval);
    }

    private void castCrescentProjectile(Player player) {
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection();

        Snowball crescentProjectile = player.getWorld().spawn(location.add(direction.multiply(2)), Snowball.class);
        crescentProjectile.setVelocity(direction.multiply(2));
        crescentProjectile.setCustomName("CrescentProjectile");

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1.0f, 1.0f);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (crescentProjectile.isDead() || !crescentProjectile.isValid()) {
                    this.cancel();
                    return;
                }

                ParticleShapes.draw(EnumShapes.CRESCENT, crescentProjectile.getLocation(), Particle.ELECTRIC_SPARK, player);
                player.getWorld().playSound(crescentProjectile.getLocation(), Sound.ENTITY_PHANTOM_FLAP, 0.5f, 1.0f);
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // Handle projectile hit
        crescentProjectile.setCustomNameVisible(false);
        crescentProjectile.setShooter(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                crescentProjectile.getNearbyEntities(1, 1, 1).stream()
                        .filter(entity -> entity instanceof LivingEntity && entity != player)
                        .forEach(entity -> {
                            LivingEntity target = (LivingEntity) entity;
                            target.damage(5.0, player);
                            if (target instanceof Monster) {
                                target.damage(7.5, player); //1.5x damage to monsters
                            }
                            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 1));
                            this.cancel();
                        });

                if (crescentProjectile.isDead() || !crescentProjectile.isValid()) {
                    this.cancel();
                }
            }

        }.runTaskTimer(plugin, 0L, 1L);
    }
}
