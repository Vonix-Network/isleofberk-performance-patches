# Changelog

## 1.3.4 — Variant Loader presence-gate successor

- Successor to the delivered 1.3.3 JAR; the 1.3.3 artifact remains unchanged.
- Changes the 13 overlapping dragon model-resource Mixin gate from exact-version-only to Variant Loader mod presence, so those Mixins are skipped for any detected `iobvariantloader` version.
- Widens the optional `iobvariantloader` dependency metadata to `[1.0.0,)` so arbitrary present versions can load and be detected by the runtime gate.
- Keeps the optional passenger-render guard separately fail-closed behind client side, exact Variant Loader 2.7.0 metadata, the exact `PassengerLayer.class` fingerprint, and the exact static method descriptor.
- Updates deterministic fixtures, packaged metadata, build descriptions, and installation documentation to match the presence-based policy.
- No new Variant Loader resource-selection override, gameplay, AI cadence, combat, RNG, networking, worldgen, or quantitative performance claim.

## 1.3.3 — Variant Loader gate hardening

- Preserves the verified 1.3.2 combined artifact scope and frozen R4/1.3 performance work.
- Skips the 13 overlapping dragon model-resource Mixins only for exact Variant Loader 2.7.0 metadata; mismatched, malformed, unavailable, or absent metadata leaves those Mixins enabled.
- Adds an exact `PassengerLayer.class` fingerprint with class-name, method-name, descriptor, and static-method checks before enabling the optional passenger guard.
- Any target fingerprint failure disables only the optional passenger guard instead of allowing a late render-time injection failure.
- Adds deterministic positive and negative gate fixtures and runs the plugin fixture through Gradle `check`.
- Keeps the riderless-only empty-passenger cancellation and the original non-empty passenger path unchanged.
- No new Variant Loader selection/cache implementation, gameplay, AI cadence, combat, RNG, networking, worldgen, or quantitative performance claim.

## 1.3.2 — Full combined performance-patches artifact

- Combines the frozen R4/1.3 performance-patch scope with the optional Variant Loader 2.7.0 passenger-render guard in one JAR.
- Adds one client-only `PassengerLayer.renderPassenger` `HEAD` cancellation for non-null empty passenger lists; non-empty passenger rendering remains on the original Variant Loader path.
- Applies the passenger guard only for exact Variant Loader 2.7.0 client metadata and fails closed on absent, malformed, mismatched, or server-side conditions.
- Preserves the existing 13-resource-Mixin overlap skip so Variant Loader remains authoritative for dynamic resource selection.
- Fixes the prior 1.3.1 Mixin validation defect by keeping Mixin helper fields private.
- The standalone render-companion JAR is not needed when this combined 1.3.2 artifact is installed.
- No Deadlock Fix, gameplay, AI cadence, combat, RNG, networking, worldgen, or Variant Loader implementation changes are included.
- No quantitative FPS, MSPT, RAM, or guaranteed performance claim is made.

## 1.3 — Targeted safe successor of remaining Vonix hot-path Mixins

V1.3 is a successor candidate and a targeted safe port of remaining historical Vonix optimizations that can be expressed as narrow Mixins. It is not a complete Vonix port. No FPS, RAM, or MSPT percentage and no guaranteed performance are claimed.

### Added

- Added per-instance `Vector3f` corner reuse and camera-position lookup reuse on the seven Isle of Berk particle `render` methods (`FireBolt`, `FireCoat`, `Flame`, `FuryBolt`, `Gas`, `SkrillLightning`, `SkrillSkill`) without cancelling or overwriting render.
- Added `FlyNodeEvaluator` neighbor `EnumMap` reuse with a guarded fresh-map fallback for mismatched scratch acquisition, and skipped the redundant second `MutableBlockPos.set` in `getBlockPathType`.
- Added client packet-handler lookup reuse for `Minecraft.getInstance()`, entity lookup, and the Skrill particle option in `ClientPacketHandlerClass.handleSpawnShockParticles`.
- Added `ClientMessageTameParticlesDragon.spawnTamingParticles` `getRandom()` lookup reuse. `nextGaussian` consumption stays in the original loop.
- Added deterministic fixtures for scratch reuse/fallback, original-jar Redirect-site counts, and mixin inventory (every declared mixin has a class; every mixin class is packaged).

### Explicitly deferred

- Projectile explosion scratch `BlockPos`, lambda hoist, fire-placement, and other combat/state paths.
- Egg tick local caching (the existing hatch-check cadence Mixin already owns `tick`) and egg `position()` inlining that cannot be expressed without method rewrite or recursive Redirect.
- Layer render rewrites (`DragonHeldItemLayer`, `LayerDragonRider`) and egg-renderer `Minecraft.getInstance()` hoists that require overwriting `render`.
- Math/interpolation method overwrites, `Util.toRadians` compile-time constants, and Catmull-Rom matrix inlining.
- Dragon-base spawn-rule caching, deadlock-named scratch `BlockPos`, `distanceTo`→`distanceToSqr` (float vs double), stream-to-get(0) rewrites, and species ability/combat getter inlining.
- AI target, taming, combat, cadence, and move-control method rewrites.
- Network handle control-flow changes (`ClientMessageGuiDragon`, `ControlMessageTerribleTerrorAbility`).
- Worldgen/spawn registration, item tooltip array allocation, ShockEffect `% 8`→`& 7` (conflicts with the configurable particle cadence Mixin), and the Nightmare fire-armor UV argument change (not equivalent).

### Compatibility and scope

- Minecraft 1.18.2, Forge 40.3.x, Java 17.
- Isle of Berk 1.2.0 and GeckoLib 3.0.57 remain required separate dependencies.
- The original Isle of Berk JAR remains required. This release does not replace or redistribute Isle of Berk.
- The Isle of Berk Deadlock Fix remains a separate companion mod and is not included in this JAR.
- When `iobvariantloader` is installed, the 13 overlapping dragon model-resource Mixins are skipped automatically so Variant Loader keeps resource selection. Those Mixins stay active when Variant Loader is absent, and when FML `LoadingModList` is null or lookup fails (fail closed). Glow, renderer, particle, packet, pathfinder, egg, saddle, and other non-overlapping Mixins are unchanged.

## 1.2.0 — Remaining verified client render-resource patches after 1.1

### Added

- Attempt FPS Improvment With multiple dragons in view.
- Added bounded static `ResourceLocation` reuse for the remaining Deadly Nadder, Gronckle, Light Fury, Monstrous Nightmare, Night Fury, Night Light, Skrill, Speed Stinger, Speed Stinger Leader, Stinger, Terrible Terror, Triple Stryke, and Zippleback model families.
- Added bounded static glow-resource reuse for Night Fury and Light Fury glow layers.
- Added per-layer saddle-resource reuse in `BaseSaddleAndChestsLayer` without replacing its render method.
- Preserved the original model variant/titan-wing decision before the cache is consulted.
- Preserved dynamic and unknown-resource fallback allocation rather than forcing unverified cache hits.
- Added a deterministic render-resource cache fixture covering known-path identity reuse, non-Isle-of-Berk fallback, and dynamic-path fallback.

### Explicitly not included

- Deadlock Fix behavior, chunk-generation guards, or Variant Loader changes.
- Broad renderer-layer rewrites, particle-render method overwrites, or client packet-handler rewrites that cannot be proven as narrow companion Mixins without bundling upstream implementation code.
- Historical FPS/RAM/MSPT percentage claims; no matched before/after benchmark is available.

## 1.1.0 — 1.0 → 1.1

### Added

- Added a guarded GeckoLib dragon-bone lookup index for Isle of Berk dragon models. The index validates the live bone list and falls back to GeckoLib's original lookup after list mutation or duplicate-name changes.

### Retained from 1.0

- Configurable flight/follow AI movement-request throttling.
- Configurable egg hatch-check cadence.
- Configurable ShockEffect particle cadence; ShockEffect damage remains on its original cadence.
- Narrow client renderer argument reuse.
- Fixed-resource egg-animation and projectile transformations.
- Variant-compatible dynamic dragon and egg model/resource selection.

### Compatibility and scope

- Minecraft 1.18.2, Forge 40.3.x, Java 17.
- Isle of Berk 1.2.0 and GeckoLib 3.0.57 remain required separate dependencies.
- The original Isle of Berk JAR remains required. This release does not replace or redistribute Isle of Berk.
- The Isle of Berk Deadlock Fix remains a separate companion mod and is not included in this JAR.
- No changes to combat rules, damage cadence, target selection, network protocol, or dynamic model selection.
- No universal FPS, MSPT, RAM, or gameplay percentage is promised; results vary by workload.
