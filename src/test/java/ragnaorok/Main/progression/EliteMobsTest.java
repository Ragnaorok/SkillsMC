package ragnaorok.Main.progression;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.persistence.*;
import org.bukkit.event.entity.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class EliteMobsTest {
 @Test void thresholdAndChanceBoundaries() {
  assertFalse(EliteMobs.wins(9,0));assertTrue(EliteMobs.wins(10,0));
  assertTrue(EliteMobs.wins(30,.04999));assertFalse(EliteMobs.wins(10,.05));
 }
 @Test void rangedMobsCannotRollVenomOrFrenzy() {
  assertFalse(EliteMobs.choices(EntityType.SKELETON).contains(EliteMobs.Trait.VENOMOUS));
  assertTrue(EliteMobs.choices(EntityType.ZOMBIE).contains(EliteMobs.Trait.VENOMOUS));
 }
 @Test void existingRollCannotBeRepeated() {
  var mob=mock(Zombie.class);var data=mock(PersistentDataContainer.class);
  when(mob.getPersistentDataContainer()).thenReturn(data);
  when(data.has(EliteMobs.ROLLED,PersistentDataType.BYTE)).thenReturn(true);
  EliteMobs.roll(mob,0,0);verify(data,never()).set(any(),any(),any());
 }
 @Test void armorReducesDirectDamage() {
  var mob=mock(Zombie.class);var data=mock(PersistentDataContainer.class);
  when(mob.getPersistentDataContainer()).thenReturn(data);
  when(data.get(EliteMobs.TRAIT,PersistentDataType.STRING)).thenReturn("ARMORED");
  var event=mock(EntityDamageByEntityEvent.class);when(event.getEntity()).thenReturn(mob);when(event.getDamage()).thenReturn(10.0);
  new EliteMobs().mitigate(event);verify(event).setDamage(8.5);
 }
 @Test void xpRoundingAndHoeTiers() {
  assertEquals(0,EliteMobs.bonusXp(0));assertEquals(8,EliteMobs.bonusXp(5));
  assertEquals(1,PlayerStats.hoeMultiplier(Material.WOODEN_HOE));
  assertEquals(1,PlayerStats.hoeMultiplier(Material.STONE_HOE));
  assertEquals(1.05,PlayerStats.hoeMultiplier(Material.IRON_HOE));
  assertEquals(1.05,PlayerStats.hoeMultiplier(Material.GOLDEN_HOE));
  assertEquals(1.10,PlayerStats.hoeMultiplier(Material.DIAMOND_HOE));
  assertEquals(1.15,PlayerStats.hoeMultiplier(Material.NETHERITE_HOE));
 }
}
