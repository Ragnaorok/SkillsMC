package ragnaorok.Main.data;

import org.junit.jupiter.api.Test;
import ragnaorok.Main.ClassType;
import ragnaorok.Main.SkillsMCPlayer;
import static org.junit.jupiter.api.Assertions.*;

class ClassSelectionTest {
    SkillsMCPlayer profile() {
        var p = new SkillsMCPlayer(null,10,10,20,30,40,ClassType.WARRIOR);
        p.setLevel(9); p.setExperience(44); p.setMana(2); p.setMaxMana(15);
        return p;
    }
    @Test void switchingAllClassesKeepsProgressAndResources() {
        var p = profile();
        for (var type : new ClassType[]{ClassType.ARCHER,ClassType.CLERIC,ClassType.MAGE,ClassType.WARRIOR}) {
            assertTrue(ClassSelection.commit(p, type, () -> true));
            assertEquals(type, p.getClassType());
            assertEquals(9,p.getLevel()); assertEquals(44,p.getExperience());
            assertEquals(2,p.getMana()); assertEquals(15,p.getMaxMana());
            assertEquals(20,p.getSouls()); assertEquals(30,p.getCurrency()); assertEquals(40,p.getBounty());
        }
    }
    @Test void failedSaveRollsBackClass() {
        var p=profile();
        assertFalse(ClassSelection.commit(p,ClassType.MAGE,()->false));
        assertEquals(ClassType.WARRIOR,p.getClassType());
        assertEquals(44,p.getExperience());
    }
    @Test void unexpectedSaveExceptionAlsoRollsBackClass() {
        var p=profile();
        assertThrows(IllegalStateException.class,()->ClassSelection.commit(p,ClassType.CLERIC,()->{throw new IllegalStateException();}));
        assertEquals(ClassType.WARRIOR,p.getClassType());
    }
    @Test void reselectingSameClassDoesNotSaveOrReset() {
        var p=profile();
        assertTrue(ClassSelection.commit(p,ClassType.WARRIOR,()->{fail("Unnecessary save");return false;}));
        assertEquals(9,p.getLevel());
    }
    @Test void noneCannotBeSelected() {
        var p=profile();
        assertThrows(IllegalArgumentException.class,()->ClassSelection.commit(p,ClassType.NONE,()->true));
        assertEquals(ClassType.WARRIOR,p.getClassType());
    }
}
