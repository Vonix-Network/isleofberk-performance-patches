# 0.3.0 release evidence

This document records the implemented scope, audited exclusions, and acceptance evidence for the 0.3.0 standalone companion release. It is not a claim that every historical Vonix fork hunk can be safely reproduced as a separate Mixin companion.

## Release identity

- Release: [v0.3.0](https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v0.3.0)
- Artifact: `isleof-berk-performance-patches-0.3.0.jar`
- Size: 46,187 bytes
- SHA-256: `c28dfc2871d97b654933f1b0d9023dd54f9319064274c3d1dd3ac2dc026efb56`
- Target: Minecraft 1.18.2, Forge 40.3.0, Java 17, Isle of Berk 1.2.0, GeckoLib 3.0.57

## Implemented surface

| Family | Status | Boundary |
|---|---|---|
| Renderer argument reuse | Implemented | Twelve exact client renderer bodies; resolved texture arguments are reused. |
| Fixed model/egg/projectile resources | Implemented | Thirty-six exact constructor-only resource methods; dynamic resources remain untouched. |
| AI movement-request cadence | Implemented | Three exact flight/follow goals; `@Unique` counters reset at the available lifecycle boundaries and only navigation/circle calls are gated. |
| Egg hatch-check cadence | Implemented | The exact upstream 20-tick constant is redirected to the COMMON config interval; hatch state updates, side effects, and order remain in the original method. |
| Shock particle cadence | Implemented | The exact particle 8-tick constant is configurable; the separate 20-tick damage constant is untouched. |
| Packet particle-handler local caching | Deferred | Client-only caching remains outside this release because it is not required for the documented companion boundary. |

## Configuration defaults

| Key | Optimized default | Upstream/normal value | Notes |
|---|---:|---:|---|
| `ai_move_throttling_enabled` | `true` | `false` | `false` restores no-throttle AI movement requests. |
| `ai_move_interval_ticks` | `4` | `1` | `1` disables AI cadence throttling. Larger values can reduce responsiveness. |
| `egg_hatch_check_interval_ticks` | `20` | `20` | The pinned IoB 1.2.0 behavior already uses 20 ticks. Lower values change timing granularity and increase work. |
| `shock_particle_interval_ticks` | `8` | `8` | The pinned IoB 1.2.0 behavior already uses 8 ticks. Lower values increase packet/client work and visual density. |

## Audited Vonix-wave disposition

- `vonix.1`: Deferred. Broad cadence/config-cache and defensive changes were not copied wholesale.
- `vonix.2`: Deferred. Deadlock and chunk-access changes are excluded.
- `vonix.3`: Deferred. Variant registration and taming UX are excluded.
- `vonix.4`: Deferred. Spawn, baseline routing, and taming changes are excluded.
- `vonix.5`: Partial. Fixed resources, renderers, and exact configurable flight movement cadence are implemented; deadlock/scratch changes remain excluded.
- `vonix.6`: Partial. Safe exact cadence surfaces are implemented; spawn, deadlock, and broad method-local rewrites remain excluded.
- `vonix.7`: Deferred. Pathfinder structure substitution is excluded.
- `vonix.8`: Deferred. Projectile ray-march scratch/predicate lifetime changes are excluded.
- `vonix.9`: Partial. Exact configurable AI movement-request surfaces are implemented; target/combat/navigation substitutions are excluded.
- `vonix.10`: Partial. Exact egg and shock cadence surfaces are implemented; entity tick/state/combat rewrites are excluded.
- `vonix.11`: Partial. Renderer and fixed-resource coverage is implemented; dynamic layers, particles, and uncertain timing changes remain excluded.
- `vonix.12`: Deferred/partial. No protocol changes are included; packet-handler caching remains deferred.
- `vonix.13`: Partial. Fixed resources and ShockEffect cadence are included; particle render/event/worldgen/item/effect changes outside the exact boundary are excluded.

## Explicit exclusions

The release does not add `distanceToSqr` substitutions, deadlock/getChunkNow behavior, network-protocol changes, spawn/worldgen changes, shared static particle arrays, pathfinder algorithm substitutions, original Isle of Berk classes/resources, or deadlock-fix classes/resources.

## Acceptance evidence

- Java 17 Gradle clean `check build --offline --no-daemon --rerun-tasks`: `BUILD SUCCESSFUL`.
- `auditPackagedJar`: companion-only archive; no bundled Isle of Berk or deadlock-fix content.
- `configFixture`: required COMMON keys, defaults, and comments present.
- `RendererBytecodeFixture`: 12 renderers and 36 fixed-resource methods passed.
- Fresh Forge server runtime: all five common gameplay mixins applied; server reached `Done (12.028s)` without `MixinApplyError` or `InvalidInjectionException`.
- Independent GPT-5.6-SOL review: `PASS`; no P0 or P1 findings.
- GitHub release asset was downloaded and matched the pinned local SHA-256 byte-for-byte.

No quantitative performance claim is made because matched before/after profiling was not performed.
