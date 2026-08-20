# Isle of Berk Performance Patches

[![Minecraft](https://img.shields.io/badge/Minecraft-1.18.2-62b47a)](https://www.minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-40.3.x-orange)](https://files.minecraftforge.net/net/minecraftforge/forge/)
[![Java](https://img.shields.io/badge/Java-17-red)](https://adoptium.net/)

A standalone Forge Mixin companion for **Isle of Berk 1.2.0** on **Minecraft 1.18.2**. It reduces selected repeated AI, particle, egg-check, renderer, and projectile work without replacing Isle of Berk or bundling its classes.

> **Status:** `1.0.1-rc.1` is a local release-candidate build. It has passed the repository build and deterministic fixtures and has booted in the disposable Claws of Berk server runtime. It is not a published GitHub release and has no quantitative performance claim until matched profiling is completed.

## What this project does

The companion applies narrow, targeted transformations to Isle of Berk's existing code:

- **AI movement-request throttling:** reduces repeated navigation requests in three pinned dragon flight/follow goals. Each goal makes one cadence decision at the beginning of its tick and reuses that decision for the gated calls in that tick.
- **Egg-check scheduling:** exposes the pinned egg hatch/warmth check interval through the common Forge configuration surface.
- **Shock particle scheduling:** exposes the particle cadence while leaving ShockEffect damage cadence fixed at 20 ticks.
- **Renderer allocation reduction:** reuses selected render-type arguments in twelve exact client renderer targets.
- **Fixed-resource allocation reduction:** reuses constants in eight pinned egg-animation and projectile constructor paths.
- **Variant Loader compatibility:** leaves dynamic dragon and egg model, texture, animation, and layer selection available for Variant Loader and other variant packs instead of forcing stock resources.

The project is a **companion**, not a fork, replacement, or redistribution of Isle of Berk. The original Isle of Berk JAR remains required.

## What it fixes or reduces

| Area | Result |
| --- | --- |
| Repeated flight/follow navigation calls | Configurable cadence gate for three exact AI goals |
| Repeated egg warmth/hatch checks | Configurable check interval |
| Repeated ShockEffect particles | Configurable particle interval |
| Renderer argument allocations | Narrow reuse in twelve client renderers |
| Egg-animation and projectile constructor constants | Narrow reuse in eight fixed-resource paths |
| Variant dragon/egg resource selection | Preserved for Variant Loader remapping |

These are performance-focused reductions, not gameplay-system replacements. The companion does **not** claim to make every Isle of Berk path asynchronous or automatically improve every workload.

## What it deliberately does not change

The companion does not change:

- damage cadence, combat rules, target selection, RNG order, or progression;
- network protocol, packet formats, or synchronization contracts;
- deadlock or chunk-access safety behavior;
- spawning, world generation, or structure placement;
- pathfinder algorithms or projectile scratch-state semantics;
- dynamic model, texture, animation, or layer selection;
- Isle of Berk classes or resources bundled in the companion.

Deadlock Fix is a separate mod. Threaded Horizons is a separate mod. This project does not include or replace either one.

## Compatibility

| Component | Supported value |
| --- | --- |
| Minecraft | `1.18.2` |
| Forge | `40.3.x`; declared range `[40.3.0,40.4.0)` |
| Isle of Berk | exactly `1.2.0` |
| GeckoLib | `3.0.57` |
| Java | `17` |

For multiplayer, install the same companion JAR on the client and server. Client renderer mixins are client-side; common gameplay mixins require matching companion versions.

## Installation

1. Install Forge for Minecraft 1.18.2.
2. Install the original `isleofberk-1.2.0.jar`.
3. Install GeckoLib Forge `3.0.57`.
4. Install `isleof-berk-performance-patches-1.0.1-rc.1.jar`.
5. Install the same companion version on both client and server for multiplayer.
6. Start once to generate `config/isleofberkperformance.toml`.

Deadlock Fix and Threaded Horizons remain separate optional artifacts and must be installed separately when used.

## Configuration

The companion registers `config/isleofberkperformance.toml` as a Forge `COMMON` configuration.

| Key | Optimized default | Normal/pinned value | Purpose |
| --- | ---: | ---: | --- |
| `ai_move_throttling_enabled` | `true` | `false` | Enables the three-goal AI movement cadence gate |
| `ai_move_interval_ticks` | `4` | `1` | Allows an eligible movement request every configured number of goal ticks; `1` restores every-tick cadence |
| `egg_hatch_check_interval_ticks` | `20` | `20` | Egg warmth/hatch check interval |
| `shock_particle_interval_ticks` | `8` | `8` | Shock particle interval; damage remains fixed at 20 ticks |

The generated configuration comments describe the trade-offs. Changing cadence values can intentionally change timing or visual frequency.

## Candidate artifact

```text
File:   isleof-berk-performance-patches-1.0.1-rc.1.jar
SHA256: 57b880f636fff7d8c1f69b310465ac13964817812586651a5531142043ab5577
```

This is a local release candidate. Do not rename a `1.0.0` JAR and treat it as `1.0.1-rc.1`.

## Verification

The candidate was built with Java 17 using:

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
./gradlew clean check build --no-daemon --rerun-tasks
```

Verified repository gates include:

- clean build and test;
- AI cadence and lifecycle fixtures;
- configuration snapshot fixture;
- renderer and mapping fixtures;
- package and provenance checks;
- performance-wave fixture;
- absence of the removed AI predicate path.

The exact candidate also booted in the disposable Forge runtime with the Claws of Berk dependency stack and reached `Done`. A clean boot proves compatibility/startup only; matched before/after profiling is still required before claiming a measured performance improvement.

## Building from source

Requires JDK 17 and the exact Isle of Berk dependency locally. The Isle of Berk JAR is not redistributed by this repository.

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
./gradlew clean check build --no-daemon --rerun-tasks
```

## Related projects

- [Deadlock Fix](https://github.com/Vonix-Network/isleofberk-deadlockfix)
- [GitHub releases](https://github.com/Vonix-Network/isleofberk-performance-patches/releases)
- [Issue tracker](https://github.com/Vonix-Network/isleofberk-performance-patches/issues)

## License and provenance

The Vonix-owned companion source and metadata are MIT licensed. Isle of Berk and GeckoLib remain separate dependencies. This repository does not redistribute the Isle of Berk JAR.
