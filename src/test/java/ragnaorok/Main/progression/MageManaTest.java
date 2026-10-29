package ragnaorok.Main.progression;
import java.util.UUID;
import org.junit.jupiter.api.*;
import ragnaorok.Main.*;
import static org.junit.jupiter.api.Assertions.*;
class MageManaTest {
    final UUID id = UUID.randomUUID();
    SkillsMCPlayer p;
    @BeforeEach void setup() { p = new SkillsMCPlayer(null,10,10,0,0,0,ClassType.MAGE); MageMana.prepare(p); }
    @AfterEach void cleanup() { MageMana.clear(); }
    @Test void growthMigrationAndClamp() {
        assertEquals(100,p.getMana()); assertEquals(100,p.getMaxMana());
        p.setMana(20);p.setLevel(30);MageMana.prepare(p);
        assertEquals(148,p.getMaxMana());assertEquals(20,p.getMana());
        assertEquals(148,MageMana.maximum(1000));
    }
    @Test void costsCooldownAndFailedCastRollback() {
        assertNull(MageMana.cast(id,p,MageMana.Spell.FIREBALL,10000,()->{}));
        assertEquals(80,p.getMana());
        assertNotNull(MageMana.cast(id,p,MageMana.Spell.FIREBALL,11000,()->fail()));
        assertEquals(80,p.getMana());
        assertThrows(IllegalStateException.class,()->MageMana.cast(id,p,MageMana.Spell.ERUPTION,11000,()->{throw new IllegalStateException();}));
        assertEquals(80,p.getMana());
        assertNull(MageMana.cast(id,p,MageMana.Spell.ERUPTION,11000,()->{}));
        assertEquals(35,p.getMana());
    }
    @Test void freeBoltWorksAtZeroButPaidSpellsDoNot() {
        p.setMana(0);
        assertNotNull(MageMana.cast(id,p,MageMana.Spell.FIREBALL,10000,()->fail()));
        assertNull(MageMana.cast(id,p,MageMana.Spell.BOLT,10000,()->{}));
        assertEquals(0,p.getMana());
    }
    @Test void regenerationDelayAndNoCatchupOrReconnectRefill() {
        MageMana.cast(id,p,MageMana.Spell.FIREBALL,10000,()->{});
        MageMana.tick(id,p,12999);assertEquals(80,p.getMana());
        MageMana.tick(id,p,13000);assertEquals(85,p.getMana());
        MageMana.tick(id,p,13001);assertEquals(85,p.getMana());
        MageMana.joined(id,90000);assertEquals(85,p.getMana());
        MageMana.tick(id,p,91000);assertEquals(85,p.getMana());
        MageMana.tick(id,p,93000);assertEquals(90,p.getMana());
        p.setClassType(ClassType.WARRIOR);MageMana.tick(id,p,95000);assertEquals(90,p.getMana());
        p.setClassType(ClassType.MAGE);MageMana.prepare(p);assertEquals(90,p.getMana());
    }
}
