# SkillsMC — RPG Plugin Beta

Choose a class, master its weapon skills, and grow stronger as you explore. SkillsMC brings class-based combat, player progression, and locally scaled hostile mobs to **Minecraft Java Edition on Paper 1.21.11**.

**Status: Beta.** Core systems are available for playtesting. Combat balance, progression rates, and features may change based on feedback.

## What’s in the beta?

- **Four playable classes:** Warrior, Archer, Mage, and Cleric.
- **Class-locked weapon skills:** special abilities require the matching class; ordinary weapon and tool use remains available.
- **Level-based progression:** earn SkillsMC XP from supported mob kills, gain health at milestones, and strengthen damaging skills.
- **Local mob levels:** eligible hostile mobs scale around nearby active players, with protection for mixed-level groups.
- **Mob equipment:** higher-level zombie and skeleton variants can spawn with armor.
- **Persistent progress:** automatic saves every ten minutes, plus saves on logout, shutdown, and confirmed class changes.
- **Player profile and guide:** check your class, level, bonuses, and balances in game.
- **Blast Axe crafting, skill effects, and basic soul collection** from certain mobs.

## Classes and abilities

| Class | Weapons | Abilities |
| --- | --- | --- |
| Warrior | Sword, dual swords, axe, shield | Gale Slash, Blade Dance, Shockwave, shield surfing and fall bounce |
| Archer | Bow, crossbow | Speed, Trueshot, Backstep, Rapidfire |
| Mage | Any hoe | Arcane Bolt, Fireball, Lava Eruption |
| Cleric | Hoe | Hallowed Ground, Crescent Projectile |

Use `/class` to choose your first class. **Only server operators can change an existing class during the beta.** Class changes preserve your level and XP. Resource-based switching is planned.

Trying another class’s special skill silently does nothing. Prepared casts and ongoing skills are cleared when you change class, log out, die, or change worlds.

For more detail, see [class skills and testing](docs/CLASS-SKILLS.md).

## Player progression

SkillsMC levels are separate from Minecraft’s vanilla XP levels. Use `/profile` to see your current level, skill bonuses, actual maximum hearts, and next health milestone.

| Class | Starting hearts | Health gained at each milestone | Skill damage per level after level 1 | Skill healing per level after level 1 |
| --- | --- | --- | --- | --- |
| Warrior | 11 | +1 heart | +1% | — |
| Archer | 10 | +½ heart | +1.25% | — |
| Mage | 9 | +½ heart | +1.5% | — |
| Cleric | 10 | +½ heart | +1% | +1% |

Health milestones occur at **levels 5, 10, 15, 20, 25, and 30**. Stat growth stops at level 30, although players can continue earning levels. Damage and healing bonuses add to the level-1 baseline rather than compounding.

Archer retains its small movement-speed bonus, and Cleric retains passive Regeneration I. Increasing maximum health does not instantly heal you. Ordinary attacks, regular bow shots, cooldowns, and movement abilities do not gain level-based damage bonuses. Gale Slash and shield abilities have no separate damage component to scale.

## Hostile mob levels

New eligible hostile mobs receive a level based on players within **128 blocks horizontally and 48 blocks vertically** of their spawn:

1. Include living Survival or Adventure players with loaded profiles and activity within the last five minutes.
2. Round their average SkillsMC level and apply **±2 levels** of random variation.
3. For a mixed-level group, cap the result at **the lowest eligible player’s level +5**.
4. Keep the final level between **1 and 35**. With no eligible players nearby, use level 1.

For example, nearby players at levels 5 and 45 produce mobs no higher than level 10. A solo level-3 player can encounter mobs from level 1 through 5.

**Creative and Spectator players do not count.** If you test alone in Creative, newly spawned mobs will be level 1. Switch to Survival, move around, and spawn fresh mobs to test variance.

A mob keeps its assigned level across chunk unloads and restarts. Moving players nearby does not rescale it. Mobs that existed before this feature remain unchanged. Hit a leveled mob to briefly see its level above your hotbar.

Each level after 1 adds **3% base health** and **1% eligible attack damage**. Passive animals, villagers, selected neutral mobs, and bosses are excluded; only the explicit hostile-mob allowlist is scaled.

Eligible zombie and skeleton variants can receive armor in empty equipment slots. Higher levels increase the chance and unlock leather, chainmail, then iron tiers. Existing equipment is preserved, and plugin-generated armor does not drop on death.

See [progression rules and test cases](docs/PROGRESSION.md) for the full allowlist, formulas, and limitations.

## Requirements

| Component | Beta target |
| --- | --- |
| Server | Paper 1.21.11 |
| Java | Java 21 |
| Required plugin | EffectLib 10.12 |

EffectLib must be installed separately. ProtocolLib is not required. Other server implementations and Minecraft versions are not validated for this beta.

The plugin currently reports `1.0-SNAPSHOT` internally. **Beta** describes the project’s development stage; **1.21.11** is the Minecraft/Paper target.

## Installation and updates

1. Back up your server and player data before installing or updating a beta build.
2. Obtain a JAR built from the `upgrade/1.21.11` branch, either from a successful GitHub Actions build or by building it yourself.
3. Stop the server fully.
4. Place EffectLib 10.12 and the SkillsMC JAR in the server’s `plugins` directory. Keep only one installed SkillsMC JAR.
5. Start the server and confirm that both plugins enable successfully.
6. Join, run `/class`, and use `/profile` and `/guide` to get started.

Use a full stop/start when updating. The optional Windows delivery pipeline stages a verified JAR for the next startup; it does not restart the server automatically. See [CI/CD setup](docs/CI-CD.md).

### Saving and backups

Player profiles remain in `<uuid>.dat` files in the server’s working directory, with a previous readable save kept as `<uuid>.dat.bak`. Saves run every **12,000 server ticks**—ten minutes at normal tick rate—and immediately on logout, graceful shutdown, and confirmed class changes. Operators can also use `/saveAll`.

An abrupt crash can lose progress since the last successful save. Keep independent backups of the server and player data. See [player data and recovery](docs/PLAYER-DATA.md).

## Commands

| Command | Purpose | Access |
| --- | --- | --- |
| `/class` | Choose a class; confirm a class change | First choice available by default; repeat changes require operator status |
| `/profile` | View your class, level, bonuses, and balances | Players |
| `/guide` | Receive the in-game guidebook | Players |
| `/setlevel <player> <level>` | Raise an online player’s SkillsMC level | Operators |
| `/saveAll` | Save all loaded player profiles | `skillsmc.save`, operators by default |
| `/souls add <player> <amount>` | Add souls to an online player | Operators |
| `/souls set <player> <amount>` | Set an online player’s soul balance | Operators |

Soul administration is still experimental; use valid online player names and nonnegative integer amounts. `/setlevel` currently raises levels only.

## Blast Axe

Craft the Blast Axe in a crafting table with an **iron axe in the top-center slot** and **flint and steel directly beneath it**. Warrior’s sprinting axe attacks can trigger Shockwave; the skill is also available with ordinary axes.

## Beta limitations and planned work

- **Souls:** basic collection and balances exist. Spending souls on skill enhancements and advanced jobs is planned.
- **Class switching:** resource costs are not implemented; repeat selection is operator-only.
- **Mana:** Mage mana casting is implemented; Mana Shield remains planned.
- **Economy:** displayed currency and bounty balances are not a complete economy or bounty system.
- **Reinforce enchantments:** disabled; `/reinforce` and `/check` are not supported beta features.
- **VFX:** existing effects remain; their rework is deferred.
- **Rewards and balance:** mob XP/soul rewards do not yet scale with mob level. High-density farms and mixed-level combat still need playtesting.
- **Login API:** Paper may log a `PlayerLoginEvent` compatibility notice for SkillsMC. The older listener disables Paper’s newer reconfiguration APIs; migration is pending.

## Building and contributing

Use **JDK 21** and **Maven 3.9+**:

```sh
mvn --batch-mode --no-transfer-progress clean verify
```

The output is `target/SkillsMC-1.0-SNAPSHOT.jar`. CI packages the delivery artifact as `SkillsMC-1.21.11.jar`.

For changes, branch from `upgrade/1.21.11`, keep the scope focused, run the build and tests, and open a pull request against that branch. Test gameplay on Paper with EffectLib as well as running automated tests.

## Feedback and bug reports

[Open an issue](https://github.com/Ragnaorok/SkillsMC/issues) with:

- Your Paper, Java, EffectLib, and SkillsMC build versions or commit.
- Your class, SkillsMC level, and game mode.
- Steps to reproduce, expected behavior, and what happened instead.
- Relevant console errors or screenshots.

For mob-scaling reports, include nearby players’ levels and game modes, whether they were active, and whether the mob spawned before or after the update.

Created by [Ragnaorok / Tung Nguyen](https://github.com/Ragnaorok).

### Mage weapons and mana

Mages cast with any hoe, including a wooden hoe. Blaze rods no longer cast Mage skills. Left-click fires a free Arcane Bolt; right-click casts the selected spell, and sneak-right-click switches between Fireball (20 mana) and Lava Eruption (45 mana). Cooldowns are 1, 3, and 10 seconds respectively.

Maximum mana starts at 100 and grows by 2 per level after level 1, capped at 158 at level 30. Regeneration restores 5 mana per second after 3 seconds without a successful cast. Current mana is saved with player data; reconnecting does not refill it. Mana and the selected spell appear in the action bar. See [Mage mana rules and testing](docs/MAGE-MANA.md).
