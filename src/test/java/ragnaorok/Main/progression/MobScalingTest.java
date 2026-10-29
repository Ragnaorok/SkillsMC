package ragnaorok.Main.progression;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MobScalingTest {
    @Test void mixedGroupCapAppliesAfterPositiveVariation() {
        for (int roll = -2; roll <= 2; roll++) {
            assertEquals(10, MobScaling.level(List.of(5, 45), roll));
            assertEquals(6, MobScaling.level(List.of(1, 100, 100), roll));
        }
        assertEquals(6, MobScaling.level(List.of(1, 9), 2));
    }
    @Test void averagesNearbyPlayersWithoutForcingEveryMobToTheCap() {
        assertEquals(8, MobScaling.level(List.of(5, 6), 2));
        assertEquals(4, MobScaling.level(List.of(5, 6), -2));
        assertEquals(22, MobScaling.level(List.of(20, 20), 2));
        assertEquals(18, MobScaling.level(List.of(20), -2));
    }
    @Test void emptyGroupsAndLimitsAreSafe() {
        assertEquals(1, MobScaling.level(List.of(), 2));
        assertEquals(1, MobScaling.level(List.of(1), -2));
        assertEquals(30, MobScaling.level(List.of(Integer.MAX_VALUE, Integer.MAX_VALUE), 2));
        assertEquals(1, MobScaling.healthMultiplier(1));
        assertEquals(1, MobScaling.damageMultiplier(1));
        assertEquals(MobScaling.healthMultiplier(35), MobScaling.healthMultiplier(1000));
        assertEquals(0, MobScaling.armorChance(1));
        assertTrue(MobScaling.armorChance(35) <= .25);
    }
}
