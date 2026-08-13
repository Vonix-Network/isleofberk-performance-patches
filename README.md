# Isle of Berk Performance Patches

[![Release](https://img.shields.io/github/v/release/Vonix-Network/isleofberk-performance-patches?label=release)](https://github.com/Vonix-Network/isleofberk-performance-patches/releases)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.18.2-62b47a)](https://www.minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-40.3.x-orange)](https://files.minecraftforge.net/net/minecraftforge/forge/)
[![Java](https://img.shields.io/badge/Java-17-red)](https://adoptium.net/)

A standalone Forge Mixin companion that applies Vonix performance patches to **Isle of Berk 1.2.0** on **Minecraft 1.18.2**.

> **This is a companion, not a fork or replacement.** Keep the original `isleofberk-1.2.0.jar` installed. This project does not redistribute Isle of Berk classes or resources and does not include the separate `isleofberk-deadlockfix` mod.

## Download

- [Latest GitHub release](https://github.com/Vonix-Network/isleofberk-performance-patches/releases/latest)
- [Version 0.3.0](https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v0.3.0)
- [Download the 0.3.0 JAR](https://github.com/Vonix-Network/isleofberk-performance-patches/releases/download/v0.3.0/isleof-berk-performance-patches-0.3.0.jar)

### 0.3.1-rc.1 release-candidate integrity

The unpublished `0.3.1-rc.1` candidate is a successor to, and does not replace, the immutable 0.3.0 release. Prior independent reviews rejected predecessor trees; this candidate requires a fresh independent Sol review and is not accepted. GitHub publication remains pending and is not authorized. The hash below is the local canonical artifact after `reobfJar` + `canonicalizeReobfJar`.

```text
File:   isleof-berk-performance-patches-0.3.1-rc.1.jar
Size:   47,478 bytes
SHA256: 7238b74e13167a59310cd1e9e14ff56048733a8755a272602a0aa6b0064a38c9
```

### 0.3.0 artifact integrity

```text
File:   isleof-berk-performance-patches-0.3.0.jar
Size:   46,187 bytes
SHA256: c28dfc2871d97b654933f1b0d9023dd54f9319064274c3d1dd3ac2dc026efb56
```

## Compatibility

| Component | Supported version |
|---|---|
| Minecraft | 1.18.2 |
| Forge | 40.3.0; version range `[40.3.0,40.3.1)` |
| Isle of Berk | Exactly 1.2.0; version range `[1.2.0,1.2.0.1)` |
| GeckoLib | 3.0.57; version range `[3.0.57,3.0.58)` |
| Java | 17 |

The original Isle of Berk JAR and GeckoLib are required dependencies. The companion's common gameplay mixins should be installed with the same companion version on both client and server for multiplayer. Renderer and model mixins load on the client only.

## Installation

1. Install Forge 40.3.0 for Minecraft 1.18.2.
2. Install the original `isleofberk-1.2.0.jar`.
3. Install GeckoLib Forge 3.0.57.
4. For the stable release, put `isleof-berk-performance-patches-0.3.0.jar` in the same `mods` directory. The `0.3.1-rc.1` candidate is unpublished; if testing it, use the exact local canonical JAR below rather than a GitHub prerelease.
5. Start the game once to generate `config/isleofberkperformance.toml`.
6. For multiplayer, install the same companion version on the client and server.

The optional `isleofberk-deadlockfix` mod remains a separate artifact. Do not merge the JARs or treat this companion as a replacement for it.

## What is included

- Configurable AI movement-request throttling for three exact Isle of Berk flight/follow goals.
- Configurable egg hatch-check cadence.
- Configurable ShockEffect particle cadence, while ShockEffect damage cadence remains fixed at 20 ticks.
- Twelve narrow client renderer argument-reuse transformations.
- Thirty-six fixed-resource model, egg, and projectile transformations.
- Strict target descriptors and production refmap mappings for the pinned Isle of Berk 1.2.0 bytecode.

These are narrow companion mixins. The original Isle of Berk behavior remains the source of all non-targeted logic.

## Configuration

Forge registers `config/isleofberkperformance.toml` as a COMMON configuration. Comments in the generated file explain the normal/upstream value, the optimized default, and the tradeoff.

| Key | Optimized default | Upstream/normal value | Effect |
|---|---:|---:|---|
| `ai_move_throttling_enabled` | `true` | `false` | Throttles repeated flight/follow navigation requests. |
| `ai_move_interval_ticks` | `4` | `1` | After a goal starts, the first eligible request runs immediately; later eligible requests use this interval. `1` disables AI cadence throttling. |
| `egg_hatch_check_interval_ticks` | `20` | `20` | The pinned IoB 1.2.0 behavior already checks egg warmth/hatch progress every 20 ticks. Lower values increase work and change timing granularity. |
| `shock_particle_interval_ticks` | `8` | `8` | The pinned IoB 1.2.0 behavior already sends shock particles every 8 ticks. Lower values increase packet/client work and visual density. |

For the upstream AI cadence baseline, set `ai_move_throttling_enabled = false` or set `ai_move_interval_ticks = 1`. Egg and shock defaults already match the pinned upstream cadence. Changing these values intentionally changes timing or visual behavior.

## Scope and explicit exclusions

This release does **not** change:

- damage cadence, combat rules, target selection, RNG order, or progression semantics;
- the Isle of Berk network protocol or packet format;
- deadlock/safety/chunk-access behavior, including `getChunkNow` and `scratchPosDeadlockFix`;
- Variant Loader compatibility, taming, spawning, or world generation;
- pathfinder algorithms or mutable projectile scratch state;
- shared static particle arrays;
- dynamic model, texture, or layer selection;
- the deferred client packet-handler local-cache optimization.

The historical Vonix waves through `1.2.1-vonix.13` were audited. The companion intentionally implements only the narrow, defensible subset documented here. See [CANDIDATE-REPORT.md](CANDIDATE-REPORT.md) for the wave-by-wave disposition.

## Verification

The 0.3.0 release was checked against the pinned original dependency with Java 17 and Gradle 7.5.1:

- `BUILD SUCCESSFUL`
- `auditPackagedJar`: companion-only archive; no bundled Isle of Berk or deadlock-fix content
- `configFixture`: required COMMON keys, defaults, and comments present
- `RendererBytecodeFixture`: 12 renderers and 36 fixed-resource methods passed
- Fresh Forge server runtime: all five common gameplay mixins applied; server reached `Done (12.028s)`
- Independent GPT-5.6-SOL review: `PASS`
- Final artifact SHA-256 matches the uploaded GitHub release asset

No quantitative performance claim is made without matched before/after profiling.

## Building from source

Requires JDK 17. The pinned local validation command is:

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:$PATH \
./gradlew clean check build --offline --no-daemon --rerun-tasks
```

The exact original Isle of Berk dependency is required locally for the bytecode fixture and is intentionally not committed or redistributed.

`reobfJar` remains required. The following `canonicalizeReobfJar` task rewrites only ZIP timestamps and timestamp extra fields on that reobfuscated JAR so two clean builds from the same tree are byte-identical. Audit and mapping fixtures inspect that canonical artifact.

## Documentation

- [Changelog](CHANGELOG.md)
- [Release evidence and audited Vonix-wave disposition](CANDIDATE-REPORT.md)
- [GitHub releases](https://github.com/Vonix-Network/isleofberk-performance-patches/releases)
- [Issue tracker](https://github.com/Vonix-Network/isleofberk-performance-patches/issues)

## License and provenance

The Vonix-owned companion source and metadata are MIT licensed. Isle of Berk and GeckoLib remain separate dependencies. This repository does not redistribute the Isle of Berk JAR.
