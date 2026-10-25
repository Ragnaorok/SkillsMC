package ragnaorok.Main.skills;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ragnaorok.Main.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ragnaorok.Main.skills.SkillAccess.WeaponSkill.*;

class SkillAccessTest {
    Player player;
    SkillsMCPlayer profile;
    @BeforeEach void setup() {
        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.isOnline()).thenReturn(true);
        profile = new SkillsMCPlayer(player,10,10,0,0,0,ClassType.NONE);
        Constant.SKILLS_MC_PLAYER_HASH_MAP.put(player.getUniqueId().toString(),profile);
    }
    @AfterEach void cleanup() { SkillAccess.shutdown(); Constant.SKILLS_MC_PLAYER_HASH_MAP.clear(); }
    @ParameterizedTest @EnumSource(SkillAccess.WeaponSkill.class)
    void onlyOwningClassMayCast(SkillAccess.WeaponSkill skill) {
        for (ClassType type : ClassType.values()) {
            profile.setClassType(type);
            assertEquals(type == skill.owner, SkillAccess.allows(player,skill),skill+" / "+type);
        }
    }
    @Test void missingProfileAndOperatorFailClosed() {
        when(player.isOp()).thenReturn(true);
        Constant.SKILLS_MC_PLAYER_HASH_MAP.clear();
        assertFalse(SkillAccess.require(player,BOW));
        assertThrows(IllegalStateException.class,()->SkillAccess.session(player,BOW));
    }
    @Test void deniedMessagesAreRateLimitedAndDoNotMutateProfile() {
        assertFalse(SkillAccess.require(player,BOW)); assertFalse(SkillAccess.require(player,HOE));
        verify(player,times(1)).sendMessage(anyString());
        assertEquals(ClassType.NONE,profile.getClassType()); assertEquals(10,profile.getMana());
    }
    @Test void invalidationCannotBeBypassedBySwitchingBack() {
        profile.setClassType(ClassType.ARCHER);
        var old=SkillAccess.session(player,BOW);
        assertTrue(old.valid(BOW));
        SkillAccess.invalidate(player);
        profile.setClassType(ClassType.WARRIOR);
        profile.setClassType(ClassType.ARCHER);
        assertFalse(old.valid(BOW)); assertTrue(SkillAccess.session(player,BOW).valid(BOW));
    }
    @Test void offlineDeadOrWrongClassStopsOngoingSkill() {
        profile.setClassType(ClassType.MAGE);var session=SkillAccess.session(player,BLAZE_ROD);
        when(player.isOnline()).thenReturn(false);assertFalse(session.valid(BLAZE_ROD));
        when(player.isOnline()).thenReturn(true);when(player.isDead()).thenReturn(true);assertFalse(session.valid(BLAZE_ROD));
        when(player.isDead()).thenReturn(false);profile.setClassType(ClassType.CLERIC);assertFalse(session.valid(BLAZE_ROD));
    }
    @Test void invalidationRemovesOwnedEntitiesAndPreparedState() {
        profile.setClassType(ClassType.CLERIC);var session=SkillAccess.session(player,HOE);
        Entity projectile=mock(Entity.class);when(projectile.isValid()).thenReturn(true);
        Runnable cleanup=mock(Runnable.class);session.track(projectile);session.onClose(HOE, cleanup);
        SkillAccess.invalidate(player);SkillAccess.invalidate(player);
        verify(projectile,times(1)).remove();verify(cleanup,times(1)).run();assertFalse(session.valid(HOE));
    }
}
