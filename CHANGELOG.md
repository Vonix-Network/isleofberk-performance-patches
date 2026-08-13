# Changelog

All notable release changes for Isle of Berk Performance Patches are recorded here.

## [0.3.1-rc.1] — release candidate (unpublished)

Successor release candidate. The published 0.3.0 artifact remains immutable. Independent Sol accepted the exact unpublished candidate artifact bytes; GitHub publication remains pending.

- Pin egg hatch-check and ShockEffect particle `ModifyConstant` injectors to the verified ordinals; the ShockEffect damage 20 is still untouched.
- Harden AI redirect counts to the pinned invoke counts (`moveTo` 1 and 5, `circleEntity` 2).
- Reset AI counters from the exact `WrappedGoal` start/stop transition instead of target lifecycle method merges or `canUse()`, because vanilla `Goal.canContinueToUse()` delegates to `canUse()`.
- First eligible AI movement request after a goal starts runs immediately, then every configured interval.
- Couple the config fixture to `PerformanceConfig.SPEC` defaults in source.

### Additional hardening

- Reset AI movement counters from the exact `WrappedGoal` start/stop transition without merging target lifecycle methods.
- Use the production-remapped `WrappedGoal.getGoal()` accessor (`m_26015_()` in the SRG runtime).
- Add deterministic mapping and lifecycle fixtures covering the prior production and runtime failure modes.
- Canonicalize ZIP timestamps/extra metadata on the final reobfuscated JAR after `reobfJar` so clean builds are byte-identical without changing entry contents, order, manifest, refmap, or bytecode.

Release-candidate artifact: 47,478 bytes, SHA-256 `7238b74e13167a59310cd1e9e14ff56048733a8755a272602a0aa6b0064a38c9`. This hash is the local canonical candidate only. Independent Sol accepted these exact unpublished bytes; publication remains pending.

## [0.3.0](https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v0.3.0) — 2026-08-13

### Added

- Forge COMMON configuration at `config/isleofberkperformance.toml`.
- Configurable AI movement-request throttling for three exact flight/follow goals.
- Configurable egg hatch-check cadence.
- Configurable ShockEffect particle cadence.
- Documentation for upstream/normal values, optimized defaults, and timing/visual tradeoffs.
- Twelve narrow client renderer argument-reuse transformations.
- Thirty-six fixed-resource model, egg, and projectile transformations.

### Compatibility and safety

- Validated against Minecraft 1.18.2, Forge 40.3.0, Java 17, Isle of Berk 1.2.0, and GeckoLib 3.0.57.
- The original Isle of Berk JAR remains a required separate dependency.
- The optional `isleofberk-deadlockfix` mod remains separate and is not bundled or replaced.
- ShockEffect damage cadence remains fixed at 20 ticks.
- Network protocol, deadlock/chunk access, spawning, world generation, pathfinder algorithms, and dynamic resource selection remain outside this release.

### Verification

- Gradle clean `check build`: passed.
- Package audit: passed; companion-only archive.
- Config fixture: passed.
- Renderer/resource fixture: passed for 12 renderers and 36 fixed-resource methods.
- Fresh Forge server runtime: all five common gameplay mixins applied and server reached `Done (12.028s)`.
- Independent GPT-5.6-SOL review: passed.
- Release asset SHA-256: `c28dfc2871d97b654933f1b0d9023dd54f9319064274c3d1dd3ac2dc026efb56`.

## [0.2.0]

- Added the narrow renderer and fixed-resource optimization wave that was promoted into the 0.3.0 companion.

## [0.1.2](https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v0.1.2)

- Published the earlier partial companion release. It remains immutable and is superseded by 0.3.0.

[0.3.1-rc.1]: https://github.com/Vonix-Network/isleofberk-performance-patches/compare/v0.3.0...HEAD
