# Player progression and local mob levels

This update targets Paper 1.21.11. Stat growth is derived from the existing player level and class. Player levels can continue past 30, but stat growth stops at 30. Existing UUID data files and their Java serialization identity are preserved.

## Player growth

| Class | Starting hearts | Bonus hearts at levels 5, 10, 15, 20, 25, 30 | Skill damage per level after 1 | Skill healing per level after 1 |
| --- | --- | --- | --- | --- |
| Warrior | 11 | +1 at each milestone | +1% | None |
| Archer | 10 | +0.5 at each milestone | +1.25% | None |
| Mage | 9 | +0.5 at each milestone | +1.5% | None |
| Cleric | 10 | +0.5 at each milestone | +1% | +1% |
| No class | 10 | None | None | None |

At level 30 a Warrior has 17 hearts; Archer and Cleric have 13, and Mage has 12. Damage bonuses are additive over the level-1 base, not compounded each level. Health is recalculated on level-up, login, respawn, successful class change, and plugin startup. Raising max health does not grant a free heal; decreasing it clamps current health.

Damage growth applies to Shockwave, Blade Dance, Trueshot, Rapidfire arrows, Fireball, Lava Eruption, and Crescent Projectile. Gale Slash and shield skills currently have control/movement effects without their own separate damage component, so they do not gain damage. Ordinary attacks/shots, movement, cooldowns, invulnerability durations, mana, and passive regeneration are unchanged.

Hallowed Ground retains two healing splashes, distance falloff, and vanilla healing-potion damage to undead. Its healing is multiplied by Cleric growth; normal potions are untouched. Crescent Projectile applies one 7.5-base hit to monsters or one 5-base hit to other living targets, fixing the previous two competing damage calls.

/profile shows actual max hearts, level, skill damage/healing bonuses, and the next health milestone. Initial class selection uses skillsmc.class.select. Repeat class changes now require operator status explicitly, even if a permissions plugin grants skillsmc.class.change.

## Mob assignment

Only newly spawned eligible hostile mobs receive levels. Existing unmarked mobs stay unchanged. Each mob stores its level in persistent entity data and keeps it across unloads/restarts; logging in or moving nearby does not rescale an existing mob.

For each eligible spawn:

1. Find players in the same world within a 128-block horizontal radius and 48 blocks vertically.
2. Include living, online Survival/Adventure players with loaded profiles who moved, interacted, built/mined, or attacked in the last five minutes. Joining counts as activity. Passive damage taken and looking around alone do not count.
3. Round the average of those players' actual levels and add a random integer from -2 through +2.
4. If their levels differ, cap the result at the lowest eligible level plus 5, AFTER random variation.
5. Clamp the final mob level to 1–35. With no eligible players, use level 1.

Examples: levels 5 and 45 yield mobs capped at 10; levels 1 and 45 yield mobs capped at 6. Equal-level or solo groups use normal +/-2 variation. The cap applies at spawn, not when a lower-level player subsequently encounters an existing mob.

This first version intentionally caps mobs at 35 to match the level-30 player growth cap. Health gains are +3% per mob level after 1; direct attack/projectile/explosion damage gains are +1%. Level 35 therefore has 2.02x base health and 1.34x eligible damage. Speed, explosion radius, potion status effects, and fire/poison ticks are unchanged. Health uses a named attribute modifier, and projectile damage snapshots the shooter's level. Eligible transformations retain the original level; newly spawned slime children receive a fresh assignment.

The allowlist includes ordinary zombie/skeleton variants, creepers, spiders, silverfish, endermites, slimes, magma cubes, phantoms, blazes, breezes, ghasts, guardians, pillagers, vindicators, evokers, vexes, witches, ravagers, piglin brutes, hoglins, zoglins, and shulkers. Spiders retain vanilla daylight behavior. Passive animals, villagers, neutral endermen/piglins/zombified piglins, and bosses (including elder guardians) are excluded. New entity types outside the list remain unscaled.

A mob's level appears briefly in the attacker's action bar when hit. No display entities or custom names are added, preserving natural despawning and existing custom names.

## Armor

Eligible zombie and skeleton variants roll each EMPTY armor slot once at spawn. Existing equipment is preserved. Chance per empty slot is 0.75 percentage points per level after 1, capped at 25%. Tiers are leather (1–9), chainmail (10–19), and iron (20+). No diamond/netherite or additional enchantments are generated.

Generated items are tagged, have zero equipment drop chance, and are filtered from death drops. This avoids farming generated armor, including through looting. Existing equipment drops retain their original behavior. Other mob types do not get generated armor.

## Scope and performance

No repeating mob scans, per-mob level timers, chunk loads, or new disk writes per spawn are introduced. Nearby-player queries run at spawn, so crowded spawner farms still need profiling. Higher health can increase the number of mobs alive. XP/soul rewards remain unchanged in this first balance pass; soul upgrades and VFX changes are deferred.

Ten-minute autosaves, immediate logout saves, shutdown saves, and critical class-selection saves remain enabled.

## In-game acceptance checks

1. Back up test player data. Compare /profile at levels 1, 5, 10, 30, and above 30. Verify reconnect/respawn do not stack bonuses; class changes never heal. /setlevel currently only raises levels.
2. Check each damaging class skill against the same mob type. Normal attacks/shots must retain baseline damage. Check Hallowed Ground healing, undead damage, and ordinary healing potions.
3. With Survival players at 5 and 45 nearby, spawn fresh zombies; all must be at most 10. With 1 and 45, all must be at most 6. Both players must be active.
4. Move a player beyond the horizontal/vertical radius and verify NEW mobs use the remaining players. Creative/Spectator/AFK players must not influence levels.
5. Move away, unload/reload chunks, and restart: existing mob levels must stay fixed. Check zombie-to-drowned and skeleton-to-stray conversions for preserved levels and non-stacking health.
6. Check cows, villagers, endermen, neutral piglins, and bosses remain unscaled. Check ranged attacks and creeper damage, with no increased explosion radius.
7. Spawn multiple high-level zombie/skeleton variants: armor is probabilistic, not guaranteed. Check existing gear is preserved and generated armor never appears in death drops, including with Looting.
8. Confirm mobs still despawn normally and existing names are preserved. Profile normal play and a crowded mob farm with Spark before expanding deployment.
