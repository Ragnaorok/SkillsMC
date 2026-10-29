package ragnaorok.Main.listeners.ToolListeners;

import org.bukkit.util.Vector;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArrowParryListenerTest {
    @Test void incomingArrowIsEligible() {
        assertTrue(ArrowParryListener.incoming(new Vector(0,0,-2),new Vector(0,0,-3)));
    }
    @Test void OutgoingAndSidewaysArrowsAreExcluded() {
        assertFalse(ArrowParryListener.incoming(new Vector(0,0,2),new Vector(0,0,-3)));
        assertFalse(ArrowParryListener.incoming(new Vector(2,0,0),new Vector(0,0,-3)));
    }
    @Test void stationaryArrowIsExcluded() {
        assertFalse(ArrowParryListener.incoming(new Vector(),new Vector(0,0,-3)));
    }
}
