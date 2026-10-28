# Mage hoe casting

Any hoe can be used by Mage, including the wooden starter hoe. Blaze rods no longer cast spells. Cleric retains its separate hoe skills, selected by class.

| Input | Action |
| --- | --- |
| Left-click | Arcane Bolt: free, 3 base damage, 1-second cooldown |
| Right-click | Cast selected spell |
| Sneak + right-click | Toggle Fireball / Lava Eruption |
| Fireball | 20 mana, 3-second cooldown |
| Lava Eruption | 45 mana once per cast, 10-second cooldown |

Mana starts at 100 and gains 2 maximum per level after level 1, capped at 158 at level 30. Current mana does not increase merely because the maximum grows. Regeneration restores 5 per second after a 3-second pause following any successful cast, including the free bolt. Cooldowns are per spell. Timing is checked on the server's one-second HUD task, so regeneration can begin up to one tick-task interval after the delay.

Legacy unused mana pools below 100 migrate once to a full Mage pool. Existing serialized fields are reused, preserving the player-data format. Current mana saves with normal player saves. Reconnect and class changes preserve spent mana; respawn restores the Mage pool. Offline regeneration is disabled. Runtime cooldowns reset on server restart; mana does not.

Casting cancels hoe interactions for Mage to prevent tilling. Other classes retain ordinary tool behavior; Cleric abilities remain unchanged. Spell selection and mana appear alongside level/XP. Errors appear in the action bar with a short sound, without chat spam. Arcane Bolt expires after two seconds and is removed on class change, death or logout. Mana Shield is not included.

## Acceptance checks

- Cast all three spells with a wooden hoe; verify blaze rods do nothing and Cleric hoe skills still work.
- Check 100 mana at level 1 and 158 maximum at level 30; level growth must not refill spent mana.
- Check exact spell costs, per-spell cooldowns, insufficient-mana rejection and free bolts at zero mana.
- Wait without casting: after the three-second delay, regain 5 per second. Repeated bolts delay regeneration too.
- Spend mana, disconnect/reconnect, switch classes away/back and restart: verify the saved pool is not refilled.
- Die/respawn as Mage: verify full mana and no lingering skill entities.
