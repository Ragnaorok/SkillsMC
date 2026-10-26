package ragnaorok.Main.progression;

import java.util.List;
import org.bukkit.entity.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.*;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class SkillScalingTest {
    @Test void onlyMarkedFireballsGetDamageBonus() {
        var fireball = mock(Fireball.class);
        var data = mock(PersistentDataContainer.class);
        when(fireball.getPersistentDataContainer()).thenReturn(data);
        var event = mock(EntityDamageByEntityEvent.class);
        when(event.getDamager()).thenReturn(fireball);
        when(event.getDamage()).thenReturn(6.0);
        var listener = new SkillScaling(type -> false);
        listener.damage(event);
        verify(event, never()).setDamage(anyDouble());
        when(data.get(SkillScaling.DAMAGE, PersistentDataType.DOUBLE)).thenReturn(1.435);
        listener.damage(event);
        verify(event).setDamage(6 * 1.435);
    }
    @Test void ordinaryAttacksAreNotScaled() {
        var event = mock(EntityDamageByEntityEvent.class);
        when(event.getDamager()).thenReturn(mock(Player.class));
        new SkillScaling(type -> false).damage(event);
        verify(event, never()).setDamage(anyDouble());
    }
    @Test void ordinaryPotionsAreUntouched() {
        var event = mock(PotionSplashEvent.class);
        var potion = mock(ThrownPotion.class);
        when(event.getPotion()).thenReturn(potion);
        when(potion.getPersistentDataContainer()).thenReturn(mock(PersistentDataContainer.class));
        new SkillScaling(type -> false).splash(event);
        verify(event, never()).setCancelled(anyBoolean());
        verify(event, never()).getAffectedEntities();
    }
    @Test void hallowedGroundHealsOnceAtScaledVanillaIntensity() {
        var event = mock(PotionSplashEvent.class);
        var potion = mock(ThrownPotion.class);
        var data = mock(PersistentDataContainer.class);
        when(event.getPotion()).thenReturn(potion);
        when(potion.getPersistentDataContainer()).thenReturn(data);
        when(data.get(SkillScaling.HEALING, PersistentDataType.DOUBLE)).thenReturn(1.29);
        var target = mock(Player.class);
        when(event.getAffectedEntities()).thenReturn(List.of(target));
        when(event.getIntensity(target)).thenReturn(.5);
        new SkillScaling(type -> false).splash(event);
        verify(event).setCancelled(true); // Vanilla must not heal a second time.
        verify(target).heal(2 * 1.29, EntityRegainHealthEvent.RegainReason.MAGIC);
        verify(target, never()).damage(anyDouble());
    }
}
