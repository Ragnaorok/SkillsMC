# Elite mobs and hoe balance

New leveled hostile mobs at level 10+ have a 5% spawn chance to receive one compatible elite trait. The roll (including failure), trait, triggers and cooldowns are persisted. Existing mobs are not retroactively rolled. Conversions preserve elite state. Natural despawning and names remain unchanged. There are no polling tasks or constant particles.

- Frenzied: once a nonlethal hit leaves health below 40%, gain 15% movement speed for the rest of that life.
- Armored: 15% less direct entity damage and 10% slower movement where the mob has a movement-speed attribute. Environmental damage is unchanged.
- Venomous: successful melee hits apply Poison I for 2 seconds, with an 8-second mob-wide cooldown.
- Last Stand: once a nonlethal hit leaves health below 25%, reduce subsequent direct entity damage by 25% for 4 seconds. It does not prevent a lethal hit.

Frenzied and Venomous are restricted to common ground melee types; other eligible hostiles choose Armored or Last Stand. Triggered traits give brief particle feedback; Frenzied and Last Stand also play a sound. Hitting elites shows the trait alongside the mob level in the action bar. Supported kill XP is multiplied by 1.5 and rounded to the nearest integer; unsupported zero-XP mobs still give zero. Vanilla XP, souls, loot and mob levels are unchanged. Player XP still stops at level 25.

Mage and Cleric damaging skills multiply their existing class/level damage by the casting hoe's material: wood/stone 1.00, copper 1.025, iron/gold 1.05, diamond 1.10, netherite 1.15. Damage is captured at cast time, so swapping hoes mid-flight or during eruption cannot change it. Ordinary melee damage, healing, mana costs and cooldowns are unchanged.

## In-game checks

Spawn fresh eligible mobs near active Survival level-10+ players; a sample should contain occasional elites, not exactly five per hundred. Check no elite spawns below level 10. Verify trait names on hit, each threshold/cooldown, bonus XP, conversion and chunk unload/restart persistence. Check no added names or forced persistence prevents despawning. Compare each hoe tier on the same target at the same player level, including switching weapons after casting. Use backups and a test world for balance testing.
