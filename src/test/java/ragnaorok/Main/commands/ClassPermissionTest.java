package ragnaorok.Main.commands;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;
import ragnaorok.Main.*;
import static org.mockito.Mockito.*;

class ClassPermissionTest {
    @AfterEach void clear() { Constant.SKILLS_MC_PLAYER_HASH_MAP.clear(); }
    @Test void grantedChangePermissionCannotBypassOperatorRequirement() {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.hasPermission("skillsmc.class.change")).thenReturn(true);
        when(player.isOp()).thenReturn(false);
        Constant.SKILLS_MC_PLAYER_HASH_MAP.put(player.getUniqueId().toString(),
                new SkillsMCPlayer(player, 10, 10, 0, 0, 0, ClassType.WARRIOR));
        new ClassCommand(null).onCommand(player, null, "class", new String[0]);
        verify(player).sendMessage("You do not have permission to change class.");
        verify(player, never()).openInventory(any(org.bukkit.inventory.Inventory.class));
    }
}
