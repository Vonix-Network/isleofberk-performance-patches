# Changelog

All notable changes to Isle of Berk Performance Patches are documented here. The project follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and Semantic Versioning where a version is published. Unpublished candidate sections are labeled explicitly and are not release claims.

## [Unreleased]

No changes beyond the current 1.3.7 development candidate are recorded.

## [1.3.7] — unpublished development candidate, September 4, 2026

This source snapshot is public development status. It is not a GitHub release, CurseForge release, deployment confirmation, or performance-proof claim.

### Added

- Added the configurable `structure_cache_max_entries` COMMON setting with default `512` and inclusive bounds `64..4096`.
- Added a reload-correct primitive snapshot for the structure-cache bound.
- Added the server-only `StructureManagerCacheMixin` using a bounded Guava `softValues()` cache at Mixin priority `900`, preserving the intended ordering after higher-priority cache replacement work.
- Added deterministic StructureManager cache contract coverage for configured-bound usage, soft values, server-only scope, and no full-lifecycle overwrite.
- Added public status documentation covering the live 1.3.7 observation and evidence limits.

### Changed

- Updated version metadata and public descriptions to identify the source as the 1.3.7 development line.
- Preserved the full earlier companion source line: AI cadence, lifecycle, bone lookup, renderer/resource reuse, particle scratch reuse, pathfinder scratch reuse, client lookup reuse, and Variant Loader compatibility gates.
- Preserved companion-only packaging and the separate Deadlock Fix boundary.

### Verification status

- Candidate packet recorded Java 17 clean `check build`, packaged JAR audit, and deterministic fixture passes.
- Candidate artifact identity recorded as 85,892 bytes with SHA-256 `389d6576ad09bca8406af889f470903ad6af93a9b4fa0aa3c61a8eb8f7d29acf`.
- Live observation was early and post-restart. Matched RAM/MSPT/GC evidence, fresh heap evidence, complete exact-candidate full-pack readiness, and formal final-candidate acceptance remain open.

### Not included

- No Lootr persistence, UUID/score-data, world-data, Threaded Horizons, or third-party JAR changes.
- No Deadlock Fix behavior, deployment, server restart, CurseForge upload, stable release, or universal RAM/MSPT/FPS claim.

## [1.3.6] — unpublished candidate, September 3, 2026

### Added

- Added the first combined performance-patches candidate line with a configurable StructureManager cache bound of `512`, clamped to `64..4096`.
- Retained a Guava soft-value cache so the JVM can reclaim parsed templates under memory pressure.
- Added the candidate-side StructureManager cache fixture and kept the earlier performance-patch fixtures in the `check` graph.

### Changed

- Carried forward the v1.3.4 companion behavior and Variant Loader compatibility gates without changing the separate Deadlock Fix boundary.
- Kept the cache setting as a restart-time server-side experiment rather than modifying persistent world or third-party data.

### Verification status

- Parent clean-build, package, and deterministic fixture gates passed in the candidate packet.
- No matched production workload A/B, fresh heap dominator/GC-root evidence, or full-pack exact-candidate runtime gate was available.
- This candidate was not published or deployed.

## [1.3.5] — unpublished candidate, August 29, 2026

### Added

- Added the first combined StructureManager cache candidate using a low-priority constructor-return hook and Guava `softValues()` with a bounded `64`-entry cache.
- Added deterministic source/package coverage for the standalone cache behavior.

### Changed

- Kept the change inside the existing performance-patches companion rather than editing the original Isle of Berk JAR.
- Preserved the existing AI, renderer, resource, particle, pathfinder, packet, and Variant Loader work.

### Verification status

- Parent build/package evidence passed and limited disposable boot evidence was recorded.
- Exact production A/B memory evidence and independent review were unavailable.
- This candidate was not published, uploaded, deployed, or restarted on the live server.

## [1.3.4] — 2026-08-29

### Changed

- Changed the 13 overlapping dragon model-resource Mixin gate from exact-version-only to Variant Loader mod presence, so those Mixins are skipped for any detected `iobvariantloader` version.
- Widened the optional `iobvariantloader` dependency metadata to `[1.0.0,)` so arbitrary present versions can load and be detected by the runtime gate.
- Kept the optional passenger-render guard separately fail-closed behind client side, exact Variant Loader 2.7.0 metadata, the exact `PassengerLayer.class` fingerprint, and the exact static method descriptor.
- Updated deterministic fixtures, packaged metadata, build descriptions, and installation documentation to match the presence-based policy.

### Not included

- No new Variant Loader resource-selection override, gameplay, AI cadence, combat, RNG, networking, worldgen, or quantitative performance claim.

## [1.3.3] — internal candidate, not separately published

### Changed

- Preserved the verified 1.3.2 combined artifact scope and frozen V1.3 performance work.
- Hardened Variant Loader overlap handling around exact 2.7.0 metadata and the passenger target fingerprint.
- Made fingerprint failure disable only the optional passenger guard instead of allowing a late render-time injection failure.

### Added

- Added positive and negative deterministic Variant Loader gate fixtures and included the plugin fixture in Gradle `check`.
- Kept riderless-only empty-passenger cancellation and the original non-empty passenger path unchanged.

## [1.3.2] — internal candidate, not separately published

### Added

- Combined the frozen V1.3 performance-patch scope with the optional client-only Variant Loader 2.7.0 passenger-render guard in one JAR.
- Added one `PassengerLayer.renderPassenger` HEAD cancellation for non-null empty passenger lists; non-empty passenger rendering remains on the original Variant Loader path.
- Added exact Variant Loader metadata/version and physical-client gating, failing closed on absent, malformed, mismatched, or server-side conditions.

### Fixed

- Fixed the earlier 1.3.1 Mixin validation defect by keeping Mixin helper fields private.

### Not included

- No Deadlock Fix, gameplay, AI cadence, combat, RNG, networking, worldgen, or Variant Loader implementation changes.

## [1.3.1] — internal predecessor, not separately published

### Changed

- Internal predecessor to the combined 1.3.2 candidate. Its Mixin validation issue was corrected in 1.3.2 rather than promoted as a standalone release.

## [1.3.0] — internal V1.3 development line, not separately published

### Added

- Added narrow hot-path Mixins for per-instance particle-render scratch reuse and camera-position lookup reuse across seven Isle of Berk particle families.
- Added `FlyNodeEvaluator` neighbor `EnumMap` reuse with guarded fresh-map fallback and removed one redundant `MutableBlockPos.set`.
- Added client packet-handler lookup reuse for Minecraft/entity/particle-option lookups and tame-particle `getRandom()` lookup reuse.
- Added deterministic fixtures for scratch reuse/fallback, original-jar redirect-site counts, and Mixin inventory.

### Deferred

- Deferred method overwrites and broad changes involving combat, target selection, AI cadence, networking control flow, world generation, RNG, hidden mutable state, or uncertain semantics.

## [1.2.0] — internal source milestone, not separately tagged

### Added

- Added bounded static `ResourceLocation` reuse for the remaining supported Isle of Berk model families.
- Added bounded static glow-resource reuse for Night Fury and Light Fury glow layers.
- Added per-layer saddle-resource reuse without replacing the layer render method.
- Preserved original variant/titan-wing selection before cache lookup and preserved dynamic/unknown-resource fallback behavior.
- Added a deterministic render-resource cache fixture for known-path reuse and fallback preservation.

### Not included

- No Deadlock Fix behavior, chunk-generation guards, broad renderer-layer rewrites, particle-render overwrites, client packet-handler rewrites, or historical FPS/RAM/MSPT percentage claim.

## [1.1.0] — internal source milestone, not separately tagged

### Added

- Added a guarded GeckoLib dragon-bone lookup index for Isle of Berk dragon models.
- Validated the live bone list and fell back to GeckoLib's original lookup after list mutation or duplicate-name changes.

### Retained

- Retained configurable flight/follow AI movement-request throttling, egg hatch-check cadence, ShockEffect particle cadence, narrow renderer argument reuse, fixed-resource egg/projectile transformations, and Variant Loader-compatible dynamic resource selection.

## [1.0.1] — 2026-08-20

### Added

- Added `AiMoveCadence` for one allow/deny decision at each pinned AI goal `tick()` HEAD. The first eligible request after a wrapped-goal start/restart runs immediately; later due ticks use the configured interval.
- Added `PerformanceSettings` primitive snapshots. The AI enabled/interval tuple is published atomically; egg and ShockEffect intervals refresh on Forge config load/reload without reading `ForgeConfigSpec` from active hot paths.
- Added deterministic re-arm behavior when effective AI settings change during a cooldown.
- Added safe follow-goal lifecycle cleanup for the inactive `tailingDragons` map at the exact follow-goal stop tail, without replacement maps, cross-tick caches, or active-tick pruning.
- Added cadence, snapshot, lifecycle, mapping, dataflow, coexistence, packaging, identity, and performance-wave fixtures and gates.

### Changed

- Preserved twelve narrow client renderer argument-reuse Mixins, eight fixed-resource egg-animation/projectile transformations, and Variant Loader-compatible dynamic resource selection.
- Kept the published 1.0.0 predecessor artifact separate and unchanged.

### Not included

- No changes to damage, combat, target selection, RNG order, progression, networking, packet formats, spawning, world generation, pathfinder algorithms, deadlock/chunk-access safety, projectile scratch state, or dynamic variant resource selection.
- No Isle of Berk classes/resources bundled; Deadlock Fix and Threaded Horizons remain separate artifacts.

### Verification

- Exact compatibility cell: Minecraft 1.18.2, Forge 40.3.x, Java 17, Isle of Berk 1.2.0, GeckoLib 3.0.57.
- Java 17 clean `check build`, package audit, mapping/lifecycle fixtures, and exact packaged runtime evidence were recorded.
- No universal RAM/MSPT/FPS percentage was asserted.

## [1.0.0] — 2026-08-15

### Changed

- Promoted the prior companion tree to the stable 1.0.0 release.
- Preserved Variant Loader-compatible dynamic dragon and egg resource selection.
- Retained the earlier renderer, fixed-resource, AI, egg, and ShockEffect companion transformations.
- Kept the Forge compile pin at `40.3.0` and the accepted Forge range at `[40.3.0,40.4.0)`.

### Compatibility

- Supported Minecraft 1.18.2, Forge 40.3.x, Isle of Berk 1.2.0, GeckoLib 3.0.57, and Java 17.
- Kept Deadlock Fix separate and did not redistribute Isle of Berk classes.

### Artifact

- `isleof-berk-performance-patches-1.0.0.jar`
- 32,865 bytes
- SHA-256 `15df9183a49e4d83a9d5e0583cec61a5a90549d873e5a64bb0116401988667e2`

## [0.3.1-rc.3] — 2026-08-14

### Fixed

- Removed stock dragon `getModelLocation` and `getAnimationFileLocation` cancellations and stopped pinning dragon textures, allowing Variant Loader and variant packs to remap dynamic resources.
- Removed egg `getModelLocation` cancellation so Variant Loader can remap egg geometry/textures while egg animation remains a fixed constructor resource.

### Retained

- Retained twelve renderer `getRenderType` argument-reuse Mixins and FireBolt/FuryBolt constructor-constant projectile Mixins.
- Added renderer fixtures covering twelve renderers and eight remaining fixed-resource methods and rejecting reintroduction of dynamic dragon/egg resource cancellations.

## [0.3.1-rc.2] — 2026-08-14

### Changed

- Widened the declared Forge dependency from `[40.3.0,40.3.1)` to `[40.3.0,40.4.0)` so live 1.18.2 Forge 40.3.x, including 40.3.12, can load.
- Kept the compile pin at Forge 40.3.0. This was a metadata compatibility fix, not a Mixin/runtime rewrite.

## [0.3.1-rc.1] — 2026-08-13

### Fixed

- Pinned egg hatch-check and ShockEffect particle `ModifyConstant` injectors to verified ordinals while leaving ShockEffect damage at 20 ticks.
- Hardened AI redirect counts to the pinned invoke counts: `moveTo` 1 and 5, `circleEntity` 2.
- Reset AI counters from the exact `WrappedGoal` start/stop transition rather than `canUse()` or target lifecycle methods.

### Added

- Added configuration, mapping, lifecycle, renderer/resource, and ZIP metadata fixtures for the candidate.
- Added deterministic ZIP timestamp/extra-field canonicalization for reobfuscated JAR output.

## [0.3.0] — 2026-08-13

### Added

- Added Forge COMMON configuration at `config/isleofberkperformance.toml`.
- Added configurable AI movement-request throttling for three exact flight/follow goals.
- Added configurable egg hatch-check cadence and ShockEffect particle cadence; ShockEffect damage cadence remained fixed at 20 ticks.
- Added twelve narrow client renderer argument-reuse transformations and the earlier fixed-resource model, egg, and projectile transformations.
- Added companion-only packaging checks and explicit gameplay/safety exclusions.

## [0.2.0] — historical development release

### Added

- Added the narrow renderer and fixed-resource optimization wave later promoted into the 0.3.0 companion.

## [0.1.2] — 2026-08-12

### Changed

- Published the earlier partial companion release. It remains immutable and is superseded by 0.3.0.

## [0.1.1] — 2026-08-12

### Added

- Published the initial standalone Isle of Berk performance-patches companion artifact.

[Unreleased]: https://github.com/Vonix-Network/isleofberk-performance-patches/compare/v1.3.4...HEAD
[1.3.4]: https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v1.3.4
[1.0.1]: https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v1.0.1
[1.0.0]: https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v1.0.0
[0.3.1-rc.3]: https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v0.3.1-rc.3
[0.3.1-rc.2]: https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v0.3.1-rc.2
[0.3.1-rc.1]: https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v0.3.1-rc.1
[0.3.0]: https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v0.3.0
[0.1.2]: https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v0.1.2
[0.1.1]: https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v0.1.1
