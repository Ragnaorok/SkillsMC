package ragnaorok.Main.progression;

import java.io.ObjectStreamClass;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;
import ragnaorok.Main.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerStatsTest {
    @AfterEach void clear() { Constant.SKILLS_MC_PLAYER_HASH_MAP.clear(); }
    @Test void existingSaveIdentityIsUnchanged() {
        assertEquals(8279374123881837458L, ObjectStreamClass.lookup(SkillsMCPlayer.class).getSerialVersionUID());
    }
    @Test void startingHealthAndFiveLevelMilestones() {
        assertEquals(22, PlayerStats.maxHealth(ClassType.WARRIOR, 1));
        assertEquals(18, PlayerStats.maxHealth(ClassType.MAGE, 1));
        assertEquals(20, PlayerStats.maxHealth(ClassType.CLERIC, 1));
        assertEquals(0, PlayerStats.bonusHealth(ClassType.WARRIOR, 4));
        assertEquals(2, PlayerStats.bonusHealth(ClassType.WARRIOR, 5));
        assertEquals(1, PlayerStats.bonusHealth(ClassType.ARCHER, 5));
        assertEquals(10, PlayerStats.bonusHealth(ClassType.WARRIOR, 30));
        assertEquals(5, PlayerStats.bonusHealth(ClassType.MAGE, 30));
    }
    @Test void classDamageAndHealingGrowthStopAtTwentyFive() {
        assertEquals(1.24, PlayerStats.damageMultiplier(ClassType.WARRIOR, 30), 1e-9);
        assertEquals(1.30, PlayerStats.damageMultiplier(ClassType.ARCHER, 30), 1e-9);
        assertEquals(1.36, PlayerStats.damageMultiplier(ClassType.MAGE, 30), 1e-9);
        assertEquals(1.24, PlayerStats.healingMultiplier(ClassType.CLERIC, 30), 1e-9);
        for (ClassType type : ClassType.values()) {
            assertEquals(PlayerStats.damageMultiplier(type, 30), PlayerStats.damageMultiplier(type, 999));
            assertEquals(PlayerStats.maxHealth(type, 30), PlayerStats.maxHealth(type, 999));
        }
        assertEquals(1, PlayerStats.healingMultiplier(ClassType.WARRIOR, 30));
        assertEquals(1, PlayerStats.damageMultiplier(ClassType.NONE, 30));
        assertEquals(20, PlayerStats.maxHealth(ClassType.NONE, 30));
    }
    @Test void applyingHealthTwiceDoesNotStackOrHealAndSwitchClampsHealth() {
        Player player = mock(Player.class);
        when(player.getHealth()).thenReturn(20.0);
        PlayerStats.applyHealth(player, ClassType.WARRIOR, 30);
        PlayerStats.applyHealth(player, ClassType.WARRIOR, 30);
        verify(player, times(2)).setMaxHealth(32);
        verify(player, never()).setHealth(anyDouble());
        when(player.getHealth()).thenReturn(34.0);
        PlayerStats.applyHealth(player, ClassType.MAGE, 30);
        var ordered = inOrder(player);
        ordered.verify(player).setHealth(23);
        ordered.verify(player).setMaxHealth(23);
    }
    @Test void offlineLevelUpDoesNotNeedBukkitPlayer() {
        var p = new SkillsMCPlayer(null, 10, 10, 0, 0, 0, ClassType.ARCHER);
        p.addExperience(10);
        assertEquals(2, p.getLevel());
        assertEquals(0, p.getExperience());
    }
}
