# Changelog

All notable changes to Isle of Berk Performance Patches are documented here. A release entry is not publication or deployment authority.

## [Unreleased]

No unreleased changes.

## [1.0.1] — 2026-08-20

Stable promotion of the performance-patch successor to published `1.0.0`.

### Included

- Added `AiMoveCadence` for one allow/deny decision at each pinned AI goal `tick()` HEAD. On a due tick, every intended gated call may run; on a skipped tick, all gated calls are suppressed. The first eligible request after a wrapped-goal start/restart runs immediately.
- Added `PerformanceSettings` primitive snapshots. The AI enabled/interval tuple is published as one packed atomic snapshot; egg and ShockEffect intervals refresh on Forge config load/reload without calling `ForgeConfigSpec.get()` from active mixin hot paths.
- Added deterministic re-arm behavior when effective AI settings change during a cooldown.
- Preserved exact lifecycle ownership: cadence state resets at the `WrappedGoal` start/stop boundary, not through `canUse()` or target lifecycle methods.
- Implemented the safe follow-goal lifecycle wave (`P01`): the existing per-goal `tailingDragons` map and active formation reads remain intact, while the inactive map is cleared at the exact follow-goal stop tail. No replacement map, entity/world cache, cross-tick cache, or active-tick pruning was added.
- Retained twelve narrow client renderer argument-reuse mixins and eight fixed-resource egg-animation/projectile transformations.
- Preserved Variant Loader-compatible dynamic dragon and egg model, texture, animation, and layer selection.
- Added cadence, snapshot, lifecycle, mapping, dataflow, instrumented-goal, coexistence, packaging, identity, and performance-wave fixtures and gates.
- Kept the published `1.0.0` predecessor artifact separate and unchanged.

### Deliberately deferred

- `P02` scan-body rewrites for `ADragonBase.airSpaceMechanics()` and `ADragonBaseFlyingRideable.onGroundMechanics()` remain deferred because no exact fail-closed whole-method transformation was proven.
- Heightmap/navigation allocation removal in `DragonFollowPlayerFlying.tick()` remains deferred; formation semantics remain unchanged.
- `BaseLinearFlightProjectile` collision, predicate, explosion, deletion, damage, and block-griefing paths remain untouched.
- `MessageDragonFlapSounds` packet allocation/handler changes remain deferred; decode, enqueue, and packet-handled behavior are unchanged.
- No changes were made to damage, combat, target selection, RNG order, progression, networking or packet formats, spawning, world generation, pathfinder algorithms, deadlock/chunk-access safety, projectile scratch state, or dynamic variant resource selection.
- No Isle of Berk classes or resources are bundled. Deadlock Fix and Threaded Horizons remain separate artifacts.

### Release verification

- Exact compatibility cell: Minecraft 1.18.2, Forge 40.3.x, Java 17, Isle of Berk 1.2.0, GeckoLib 3.0.57.
- Java 17 clean `check build` with the offline pinned dependency set, package audit, mapping fixtures, lifecycle fixtures, and companion-only scope checks.
- Exact packaged Forge runtime gate recorded separately from source-set build evidence.
- No universal RAM/MSPT/FPS percentage is asserted; workload-specific results vary by world, entity population, JVM, render settings, and configuration.
- Artifact size and SHA-256 are recorded in `RELEASE-REPORT.md` and the GitHub release asset.

## [1.0.0] — 2026-08-15

Stable promotion of the prior companion tree. This is the published GitHub Latest; earlier `0.3.x` tags remain immutable.

- Preserved Variant Loader-compatible dynamic dragon and egg resource selection.
- Included the earlier renderer, fixed-resource, AI, egg, and ShockEffect companion transformations.
- Forge compile pin remained `40.3.0`; accepted Forge range remained `[40.3.0,40.4.0)` for live 1.18.2 Forge 40.3.x, including 40.3.12.
- Supported Minecraft 1.18.2, Forge 40.3.x, Isle of Berk 1.2.0, GeckoLib 3.0.57, and Java 17.
- Kept Deadlock Fix separate and did not redistribute Isle of Berk classes.
- Artifact: 32,865 bytes, SHA-256 `15df9183a49e4d83a9d5e0583cec61a5a90549d873e5a64bb0116401988667e2`.
- No quantitative MSPT/FPS claim.

## [0.3.1-rc.3] — 2026-08-14

Release candidate.

- Removed stock dragon `getModelLocation` / `getAnimationFileLocation` cancellations and did not pin dragon textures, allowing Variant Loader and variant packs to remap dynamic resources.
- Removed egg `getModelLocation` cancellation so Variant Loader can remap egg geo/texture; egg animation remains a fixed constructor resource.
- Retained twelve renderer `getRenderType` argument-reuse mixins and the FireBolt/FuryBolt constructor-constant projectile mixins.
- Renderer fixtures covered twelve renderers and eight remaining fixed-resource methods and rejected reintroduction of dynamic dragon/egg resource cancellations.
- No runtime client retest of exploded variants was claimed. No MSPT/FPS claim.

## [0.3.1-rc.2] — 2026-08-14

Release candidate.

- Widened the declared Forge dependency from `[40.3.0,40.3.1)` to `[40.3.0,40.4.0)` so live 1.18.2 Forge 40.3.x, including 40.3.12, can load.
- Compile pin remained Forge 40.3.0. This was a metadata compatibility fix, not a mixin/runtime rewrite.

## [0.3.1-rc.1] — 2026-08-13

Release candidate.

- Pinned egg hatch-check and ShockEffect particle `ModifyConstant` injectors to verified ordinals; ShockEffect damage at 20 ticks remained untouched.
- Hardened AI redirect counts to the pinned invoke counts: `moveTo` 1 and 5, `circleEntity` 2.
- Reset AI counters from the exact `WrappedGoal` start/stop transition rather than `canUse()` or target lifecycle methods.
- Added configuration, mapping, lifecycle, and renderer/resource fixtures.
- Canonicalized ZIP timestamps and supported timestamp extra fields for deterministic reobfuscated JAR output.
- This was an unpublished candidate and did not authorize publication or live-server deployment.

## [0.3.0] — 2026-08-13

- Added Forge COMMON configuration at `config/isleofberkperformance.toml`.
- Added configurable AI movement-request throttling for three exact flight/follow goals.
- Added configurable egg hatch-check cadence and ShockEffect particle cadence; ShockEffect damage cadence remained fixed at 20 ticks.
- Added twelve narrow client renderer argument-reuse transformations and the earlier fixed-resource model/egg/projectile transformations.
- Kept the companion-only packaging boundary and explicit gameplay/safety exclusions.

## [0.2.0]

- Added the narrow renderer and fixed-resource optimization wave later promoted into the `0.3.0` companion.

## [0.1.2] — 2026-08-12

- Published the earlier partial companion release. It remains immutable and is superseded by `0.3.0`.

[1.0.1]: https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v1.0.1
[1.0.0]: https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v1.0.0
[0.3.0]: https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v0.3.0
[0.1.2]: https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v0.1.2
