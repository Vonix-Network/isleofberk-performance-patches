# Changelog

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
