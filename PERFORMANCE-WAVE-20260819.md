# Safe Performance Wave 2026-08-19

Status: candidate-only. This note records companion-source evidence only; it makes no acceptance, deployment, or publication claim and has no external effects.

The exact Isle of Berk 1.2.0 input is SHA-256 `a4b17befb1350d6d4cd07d7fdfcb2b3cec37a5c501e1f4fb811946f3e971dfc0`.

## P01 implemented

`ADragonBaseBaseFlyingRideableGoal.tailingDragons` is the pinned per-goal `Map<UUID, ADragonBaseFlyingRideable>` and active `DragonFollowPlayerFlying.tick()` retains its one `put` and four `size()` reads. The map therefore continues to provide the active formation offset semantics. A separate exact base-goal mixin exposes only `clear()`, and an exact `DragonFollowPlayerFlying.stop()V` tail injection clears the inactive goal map after upstream stop behavior. No map replacement, active-tick pruning, cross-tick cache, entity cache, or world cache was added.

## P02 BLOCKED

`ADragonBase.airSpaceMechanics()V` and `ADragonBaseFlyingRideable.onGroundMechanics()V` are large protected scan bodies. This companion supports exact injections, redirects, constants, shadows, and accessors, but has no exact fail-closed whole-method transformation facility. Replacing either body with an overwrite or a chain of constructor redirects would be fragile and cannot prove unchanged scan extent, query order, result precedence, collision/air semantics, and cadence. No scan mixin was added. The deterministic fixture pins the radius/probe/result shapes from the original bytecode.

## Follow heightmap audit

`DragonFollowPlayerFlying.tick()V` allocates a `Vec3` and a `BlockPos` for active heightmap/navigation logic. Eliminating either with this architecture requires changing the large method body or a fragile constructor redirect; no cadence, target, navigation, heightmap, or formation behavior was changed. P01 is the only lifecycle cleanup in this path.

## BaseLinearFlightProjectile BLOCKED

The collision helper, projectile tick, predicates, explosion effects, deletion, damage, and block-griefing behavior remain untouched. No provably behavior-neutral allocation-only transformation is available without replacing or redirecting collision-path bytecode.

## MessageDragonFlapSounds DEFERRED

The exact packet handler remains unchanged: decode allocates a message and handle enqueues captured work before setting packet handled. There is no matched proof that changing allocation, enqueue behavior, cadence, or packet semantics is neutral, so no mixin was added.

## Gates

`performanceWaveFixture` verifies the pinned original digest, exact descriptors, active-map lifecycle model, unchanged scan shapes, packaged activation, no bundled IoB classes, and this candidate-only provenance note. Existing package, ZIP-integrity, and identity gates remain part of `check`.
