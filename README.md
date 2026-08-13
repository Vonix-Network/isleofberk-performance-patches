Standalone Forge Mixin companion for Isle of Berk 1.2.0 on Minecraft 1.18.2, Forge 40.3.0, Java 17, and GeckoLib 3.0.57.

## Scope

Keep the original `isleofberk-1.2.0.jar` installed. This 0.3.0 release contains only Vonix-owned Mixin classes and metadata; it does not fork, replace, or bundle Isle of Berk or the separate `isleofberkdeadlockfix` mod.

It retains twelve exact client renderer transformations and 36 fixed-resource model transformations. It additionally exposes the gameplay/visual cadence paths as common Forge config controls: three exact flight/follow AI movement request gates, egg hatch checks, and ShockEffect particle packets. AI interval `1` disables AI cadence throttling. The pinned IoB 1.2.0 egg and shock defaults are already `20` and `8`; those values preserve upstream timing. Lower egg/shock values intentionally increase check/packet frequency. Shock damage remains fixed at 20 ticks.

## Config

Forge registers `config/isleofberkperformance.toml` as a COMMON config. The generated TOML comments document upstream/normal behavior, optimized defaults, and tradeoffs:

| Key | Optimized default | Upstream/normal | Effect |
|---|---:|---:|---|
| `ai_move_throttling_enabled` | `true` | `false` | Gates repeated flight/follow navigation requests. |
| `ai_move_interval_ticks` | `4` | `1` | AI movement-request cadence; `1` disables optimization. |
| `egg_hatch_check_interval_ticks` | `20` | `20` | IoB 1.2.0 already checks egg warmth/cold every 20 ticks; lower values increase work and alter timing granularity. |
| `shock_particle_interval_ticks` | `8` | `8` | IoB 1.2.0 already sends shock particles every 8 ticks; lower values increase network/client work and visual density. |

## Explicit exclusions

This companion does not alter damage cadence, target selection, combat, RNG order, network protocol, deadlock/safety/chunk access, `getChunkNow`, `scratchPosDeadlockFix`, Variant Loader compatibility, taming or spawn behavior, worldgen, pathfinder algorithms, particle static shared arrays, or dynamic model/texture/layer selection. Packet handler local caching is deferred in this release.

All historical Vonix waves through `1.2.1-vonix.13` were audited. Source-fork hunks are intentionally not copied into this companion; [CANDIDATE-REPORT.md](CANDIDATE-REPORT.md) records each family and wave disposition.

## Install

1. Keep the original `isleofberk-1.2.0.jar` in `mods`.
2. Add this companion jar alongside it.
3. Keep `isleofberkdeadlockfix` separate and optional.

The common gameplay mixins should be installed with the same companion version on both client and server for multiplayer. Renderer/model mixins load only on the client. The companion does not alter the Isle of Berk network protocol.

## Build and verification

Requires JDK 17:

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
  PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:$PATH \
  /root/.gradle/wrapper/dists/gradle-7.5.1-bin/7jzzequgds1hbszbhq3npc5ng/gradle-7.5.1/bin/gradle clean check build --offline --no-daemon --rerun-tasks
```

`rendererBytecodeFixture` checks the pinned original jar's twelve renderer bodies and 36 fixed-resource model methods. `auditPackagedJar` rejects bundled Isle of Berk/deadlockfix classes and resources and rejects non-companion package contents.

Expected artifact after a successful Gradle run: `build/libs/isleof-berk-performance-patches-0.3.0.jar`.

## License

The companion's Vonix-owned source and metadata are MIT licensed. Isle of Berk remains a separate dependency and is not redistributed here.
