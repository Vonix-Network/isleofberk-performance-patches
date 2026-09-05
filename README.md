# Isle of Berk Performance Patches

[![Latest stable release](https://img.shields.io/github/v/release/Vonix-Network/isleofberk-performance-patches?label=latest%20stable)](https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v1.3.4)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.18.2-62b47a)](https://www.minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-40.3.x-orange)](https://files.minecraftforge.net/net/minecraftforge/forge/)
[![Java](https://img.shields.io/badge/Java-17-red)](https://adoptium.net/)

A standalone Forge Mixin companion for **Isle of Berk 1.2.0** on **Minecraft 1.18.2**.

> **Companion, not replacement.** Keep the original `isleofberk-1.2.0.jar` and GeckoLib Forge `3.0.57` installed. This project does not redistribute Isle of Berk or GeckoLib classes/resources and does not include the separate Isle of Berk Deadlock Fix mod.

## Current public status

This repository's current source branch tracks the **1.3.8 development candidate**. It is public source status, not a release announcement:

- Latest tagged stable release: **v1.3.4**, published August 29, 2026.
- 1.3.5, 1.3.6, and 1.3.7 are unpublished candidate iterations for the StructureManager cache work; 1.3.8 is the current successor candidate.
- The 1.3.7 candidate artifact is **85,892 bytes**, SHA-256 `389d6576ad09bca8406af889f470903ad6af93a9b4fa0aa3c61a8eb8f7d29acf`.
- The live Isle of Berk server was observed running the 1.3.7 JAR on September 4, 2026. Early memory readings were below the earlier high-water mark, but the post-restart window was short and matched performance/heap evidence is not available.
- Do not interpret the 1.3.7 source snapshot as accepted, released, deployed, or proven to fix a memory leak.

See [STATUS.md](STATUS.md) for the evidence-bound public status and [CHANGELOG.md](CHANGELOG.md) for the complete version history.

## Compatibility

- Minecraft `1.18.2`
- Forge `40.3.x`; compiled against `40.3.0`; accepted metadata range `[40.3.0,40.4.0)`
- Isle of Berk exactly `1.2.0`; dependency range `[1.2.0,1.2.0.1)`
- GeckoLib Forge `3.0.57`; dependency range `[3.0.57,3.0.58)`
- Java `17`
- Optional Variant Loader integration; the 13 overlapping model-resource Mixins are skipped whenever `iobvariantloader` is present, while the passenger-render guard remains exact-gated to the validated 2.7.0 target and physical client side

Install the same performance-patch version on both client and server for multiplayer. Client renderer/resource Mixins affect client rendering only.

## Included patch families

The current source carries the verified companion work from the earlier release line:

- Configurable flight/follow AI movement-request cadence with exact `WrappedGoal` lifecycle reset.
- Configurable egg hatch-check cadence and ShockEffect particle cadence; ShockEffect damage remains on its original 20-tick cadence.
- Guarded GeckoLib dragon-bone lookup reuse with mutation/duplicate fallback.
- Narrow renderer argument/resource reuse for fixed Isle of Berk paths.
- Per-instance particle scratch reuse and camera-position lookup reuse.
- Pathfinder neighbor-map scratch reuse and removal of one redundant position write.
- Client packet-handler lookup reuse without changing packet order or RNG consumption.
- Variant Loader presence-aware resource-overlap gating and exact passenger-render compatibility gating.
- Server-only StructureManager parsed-template cache replacement using a bounded Guava `softValues()` map, applied after the normal higher-priority replacement path.

## 1.3.8 StructureManager candidate

The candidate retains the COMMON setting `structure_cache_max_entries` and adds bounded expiry/maintenance:

- Default: `512`
- Inclusive range: `64..4096`
- Values are clamped in reload-correct primitive snapshots.
- The cache remains soft-valued so the JVM can reclaim templates under memory pressure.
- Entries expire 30 minutes after write, limiting stale retention during long-running structure-heavy sessions.
- The cache is explicitly maintained after `StructureManager.onResourceManagerReload`.
- The repository declaration matches Forge 1.18.2's `Optional<StructureTemplate>` values.
- The setting is read when `StructureManager` is constructed; changes require a restart.
- This is a bounded memory/performance experiment. It does not modify Lootr persistence, world data, structure definitions, Threaded Horizons, or third-party JARs.

A larger bound may reduce reparsing but can retain more cache entries. A smaller bound can increase parsing and I/O. No universal RAM, MSPT, FPS, or leak-resolution result is promised.

## Installation

1. Install Forge `40.3.x` for Minecraft `1.18.2`.
2. Install the original `isleofberk-1.2.0.jar`.
3. Install GeckoLib Forge `3.0.57`.
4. Install the same performance-patch JAR on both client and server for multiplayer.
5. If using Variant Loader, install it separately. Do not install a separate render companion when using a combined performance-patch artifact.
6. If deadlock protection is required, install the separately released Isle of Berk Deadlock Fix mod.
7. Start once to generate `config/isleofberkperformance.toml`, then review the documented tradeoffs before changing cadence or cache settings.

## Configuration

The generated `config/isleofberkperformance.toml` contains:

- `ai_move_throttling_enabled`
- `ai_move_interval_ticks`
- `egg_hatch_check_interval_ticks`
- `shock_particle_interval_ticks`
- `structure_cache_max_entries`

Cadence values intentionally affect timing or visual density. The structure-cache value affects only the in-memory cache bound and takes effect after restart.

## Verification and scope

The source includes deterministic Gradle fixtures for mapping, lifecycle, packaging, Mixin inventory, Variant Loader gates, hot-path contracts, and the StructureManager cache contract. The 1.3.7 candidate packet recorded a passing clean `check build` and packaged JAR audit.

The following remain separate gates and are not claimed here:

- Matched production workload A/B testing.
- Fresh heap dominator or GC-root evidence.
- A long normal-player soak with post-restart baseline comparison.
- Full-pack packaged dedicated-server boot for this exact candidate run.
- Client launch/render-path validation.
- CurseForge publication or live deployment.

## Links

- [Public status](STATUS.md)
- [Changelog](CHANGELOG.md)
- [Latest stable release: v1.3.4](https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v1.3.4)
- [GitHub releases](https://github.com/Vonix-Network/isleofberk-performance-patches/releases)
- [Issue tracker](https://github.com/Vonix-Network/isleofberk-performance-patches/issues)

## License and provenance

Vonix-owned companion source and metadata are MIT licensed. Isle of Berk, GeckoLib, Variant Loader, and the separate Deadlock Fix mod remain separate dependencies. This repository does not redistribute those dependency JARs.
