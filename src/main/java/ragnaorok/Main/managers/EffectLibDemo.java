package ragnaorok.Main.managers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.BleedEffect;

public class EffectLibDemo extends JavaPlugin implements Listener {
    private EffectManager effectManager;

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        // Create the Effect and attach it to the Player
        BleedEffect bleedEffect = new BleedEffect(effectManager);
        bleedEffect.setEntity(event.getPlayer());

        // Add a callback to the effect
        bleedEffect.callback = new Runnable() {

            @Override
            public void run() {
                event.getPlayer().sendMessage("You bled out..");
            }

        };
        // Bleeding takes 15 seconds
        // period * iterations = time of effect
        bleedEffect.iterations = 15 * 20;
        bleedEffect.start();
    }
}



