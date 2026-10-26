# Class selection and player saves

## Behavior

- `/class` opens a selection menu without changing any stats or progress. Choose a class and confirm; closing either menu or choosing Cancel leaves the profile unchanged.
- Switching class preserves level, XP, mana, maximum mana, souls, currency and bounty. Selecting the current class does nothing. A failed save cancels the class change.
- New players may select their first class (`skillsmc.class.select`, default everyone). Changing an existing class requires `skillsmc.class.change` (default operators).
- Passives are reapplied at join and one tick after respawn: Warrior 22 maximum health, Mage 18, Archer 0.21 walk speed, Cleric permanent Regeneration I. Other classes use 20 health and 0.20 walk speed. These values do not stack. Current health is clamped when maximum health decreases; class switching does not heal.
- The Mage mana-shield helper is still not connected to damage events; this update does not enable it. Class health/speed settings still own the base values and can conflict with another plugin modifying those values. SkillsMC recognizes its hidden permanent Regeneration I when removing Cleric effects; ordinary temporary effects are retained.

## Storage and recovery

Existing `<uuid>.dat` files remain in the server working directory. Their GZIP Java serialization format is unchanged; The player field layout and serialization identity remain unchanged, preserving compatibility. No move or migration is required.

Profiles save every 10 minutes (12,000 server ticks at 20 TPS), on disconnect, on plugin shutdown, on confirmed class change, and through `/saveAll` (`skillsmc.save`, default operators). A hard crash can lose progress since the last completed save; graceful shutdown saves current profiles.

Saves first serialize the complete profile, write a temporary sibling file, flush it, and atomically replace the destination. The last readable primary is copied to `<uuid>.dat.bak` before replacement. Atomic filesystem support is required; failures are logged rather than falling back to an unsafe overwrite. Successful disconnect saves remove the in-memory profile. Failed disconnect saves retain it for retries and reconnects without retaining the Bukkit Player object.

An unreadable primary falls back to a readable backup and logs a warning. Recovery can lose progress since that backup. If neither copy can be read, login is blocked and both files remain untouched. A genuinely new player with neither file receives a new profile. Deserialization accepts only the expected profile classes and validates the class, level and XP.

All profile access and disk writes run on the server thread. This keeps save ordering deterministic and avoids accessing Bukkit objects asynchronously. It is suitable for the current small test server; large populations or slow/OneDrive-synced storage can cause pauses. A future storage worker should accept immutable snapshots and prevent older queued saves from replacing newer saves.

## Test-server checks

Before the first restart, keep a separate copy of the server-root UUID `.dat` files. The deployment workflow backs up JARs only. Stop and start the server normally; do not use `/reload`.

1. Note `/profile` level, XP, balances and class. Open `/class`, then close it. Reopen and cancel a different class. Values must be unchanged.
2. As an operator, confirm another class. Verify progression and resources remain unchanged and only class passives change. Select the same class again and check health/speed do not stack.
3. Reconnect and respawn for each class. Verify the same passives are restored. Confirm ordinary temporary Regeneration buffs are not stripped on a non-Cleric class.
4. Gain XP while online. Run `/saveAll` or wait at least 10 minutes, then stop/start while still connected. Verify progress survives. Also check normal disconnect/reconnect.
5. With a non-operator test account, verify initial selection works and later switching is denied unless explicitly permitted.
6. On a disposable copy of the server only: save twice, stop, corrupt the test player's primary `.dat`, and restart. Verify the backup recovery warning and prior progress. With both copies corrupted, verify login is denied rather than resetting data.

Keep the self-hosted runner open for staging. Paper applies the staged update only on the next startup.

## Ten-minute save verification

- While remaining online, gain XP and check that the UUID data file updates after 12,000 ticks. At lower TPS this takes longer than ten real-time minutes. Check the file before stopping the server, since shutdown also saves.
- Gain more XP and disconnect before the next autosave. Reconnect and confirm that progress was saved immediately on logout.
- Gain XP and stop the server while connected; restart and confirm that shutdown saved the progress.
- Scheduled full-save cycles are reduced by 90% (60 to 6 per hour at 20 TPS); this is not a claim of 90% lower total server load. An abrupt crash can lose up to the progress accumulated since the last successful save, normally up to ten minutes at 20 TPS.

See [PROGRESSION.md](PROGRESSION.md) for derived level bonuses and operator-only repeat class selection. Serialized player fields and serialization identity remain unchanged.
