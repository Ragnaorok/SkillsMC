package ragnaorok.Main;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginListenerTest {
    Main plugin;
    Player player;
    SkillsMCPlayer profile;
    UUID id;

    @BeforeEach void setup() {
        plugin = mock(Main.class);
        player = mock(Player.class);
        id = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(id);
        profile = new SkillsMCPlayer(player, 10, 10, 0, 0, 0, ClassType.ARCHER);
        profile.setExperience(3);
        Constant.SKILLS_MC_PLAYER_HASH_MAP.put(id.toString(), profile);
    }

    private PlayerQuitEvent quitEvent() {
        PlayerQuitEvent event = mock(PlayerQuitEvent.class);
        when(event.getPlayer()).thenReturn(player);
        return event;
    }

    @AfterEach void cleanup() { Constant.SKILLS_MC_PLAYER_HASH_MAP.clear(); }

    @Test void logoutSavesCurrentProgressBeforeRemovingProfile() {
        when(plugin.savePlayer(id, profile)).thenAnswer(call -> {
            assertSame(profile, Constant.SKILLS_MC_PLAYER_HASH_MAP.get(id.toString()));
            assertEquals(3, profile.getExperience());
            return true;
        });
        new LoginListener(plugin).onQuit(quitEvent());
        verify(plugin).savePlayer(id, profile);
        assertFalse(Constant.SKILLS_MC_PLAYER_HASH_MAP.containsKey(id.toString()));
    }

    @Test void failedLogoutSaveRetainsProgressForRetryWithoutPlayerReference() {
        when(plugin.savePlayer(id, profile)).thenReturn(false);
        new LoginListener(plugin).onQuit(quitEvent());
        verify(plugin).savePlayer(id, profile);
        assertSame(profile, Constant.SKILLS_MC_PLAYER_HASH_MAP.get(id.toString()));
        assertEquals(3, profile.getExperience());
        assertNull(profile.getPlayer());
    }

    @Test void missingProfileDoesNotOverwriteSavedProgressOnLogout() {
        Constant.SKILLS_MC_PLAYER_HASH_MAP.clear();
        new LoginListener(plugin).onQuit(quitEvent());
        verifyNoInteractions(plugin);
    }
}
