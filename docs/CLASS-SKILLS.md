# Class restricted weapon skills

Special skills now require the owning class, including for operators. Ordinary weapon attacks, bow shots, tool use and shield blocking are not cancelled by a failed class check.

| Class | Weapon skills |
| --- | --- |
| Warrior | Sword Gale Slash, dual sword Blade Dance, axe Shockwave, shield surf and fall bounce |
| Archer | Bow Speed and Trueshot; crossbow Backstep and Rapidfire |
| Mage | Hoe Fireball and Lava Eruption |
| Cleric | Hoe Hallowed Ground and Crescent Projectile |

No-class or unavailable profiles cannot cast weapon skills. Rejected attempts are silent and do not send a chat message. Interaction skills process the main-hand event once, avoiding duplicate off-hand activations; a shield may still be carried in either hand.

Prepared casts and ongoing sessions are invalidated on successful class change, logout, death, world change and plugin shutdown. Trueshot also rechecks eligibility at release and applies only to a bow, not a crossbow. Active skill entities are removed on invalidation, and scheduled effects stop at their next execution. Already applied instantaneous damage/knockback or short potion buffs are not undone. Existing cooldown durations are retained and cooldowns survive class changes and reconnects within the same server process; they are not persisted across server restarts.

## In-game acceptance checks

1. For each listed skill, test its owning class, an incorrect class and NONE where available. Only the owner should trigger special effects. Operators have no implicit bypass.
2. Confirm ordinary melee, bow and crossbow shooting, shield blocking and tool use still work on other classes. A failed special activation must not cancel the base event or consume its cooldown.
3. Prime Trueshot, change class and switch back, then shoot. No enhanced arrow should survive the switch. Repeat with Rapidfire, logout, death and world change. Firing a crossbow must not consume a prepared bow Trueshot.
4. Change class during Blade Dance, Gale Slash, Lava Eruption and Cleric effects. Ongoing actions must stop; Blade Dance arrow protection must no longer apply. Repeated Blade Dance inputs must not stack multiple active dances.
5. Check shield fall bounce with Warrior and another class. Only Warrior receives the special fall cancellation and bounce.
6. Repeat prior progression/save tests and inspect server logs. Autosaves run every 10 minutes; disconnect, shutdown, confirmed class change, and manual saves remain enabled. Wrong-class skill attempts are silently ignored without a chat notification.
