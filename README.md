# Isle of Berk Performance Patches 1.3.4

A standalone Forge Mixin companion for **Isle of Berk 1.2.0** on **Minecraft 1.18.2**.

> This is a performance companion, not a fork or replacement. Install the original `isleofberk-1.2.0.jar`, GeckoLib 3.0.57, and Variant Loader separately. The Deadlock Fix mod is a separate companion and is not included here. This JAR does not redistribute upstream implementation classes or resources.

Version 1.3.4 is the successor to the delivered 1.3.3 JAR. It preserves the frozen R4/1.3 work and the exact Variant Loader 2.7.0 passenger-render optimization, while making the 13 overlapping resource-Mixin gate presence-based for all Variant Loader versions and widening the optional dependency metadata accordingly. The standalone render-companion JAR is not required when using this combined artifact.

## Compatibility

- Minecraft 1.18.2
- Forge 40.3.x; compiled against 40.3.0
- Isle of Berk exactly 1.2.0
- GeckoLib 3.0.57
- Java 17 target; the V1.3 build and verification gates run under JDK 17
- Variant Loader (`iobvariantloader`): the 13 dragon model-resource Mixins are skipped whenever Variant Loader is present, regardless of declared version. The passenger-render guard additionally requires exact 2.7.0 metadata, the exact 2.7.0 `PassengerLayer.class` fingerprint, and physical-client side. With Variant Loader absent, the 13 overlapping resource Mixins remain enabled; malformed or unavailable loader state fails closed for the optional passenger guard. All other V1.3 Mixins remain active.

Install the same V1.3 performance-patch JAR on both client and server for multiplayer. Client rendering mixins are client-only; common performance mixins load on both sides. Install it on the client for the render-resource changes to affect client rendering.

## 1.3.4 compatibility successor

- Preserves the delivered 1.3.3 artifact scope without adding speculative Variant Loader behavior.
- Skips the 13 overlapping dragon model-resource Mixins whenever Variant Loader is present, regardless of declared version; absent Variant Loader keeps those Mixins on the original path.
- Widens the optional `iobvariantloader` dependency range to `[1.0.0,)` so arbitrary present versions can load and be detected by the runtime gate.
- Requires an exact `PassengerLayer.class` bytecode fingerprint, class name, method name, descriptor, and static method shape before enabling the optional passenger guard. Any fingerprint failure disables only that guard.
- Adds deterministic positive and negative gate fixtures and includes the plugin fixture in the Gradle `check` graph.
- Keeps the riderless-only early return and the complete non-empty passenger path unchanged.
- Does not add model/resource cache duplication, Variant Loader selection overrides, renderer call-site bypasses, gameplay changes, or quantitative performance claims.

## 1.3.2 combined update

- Retains the complete frozen R4/1.3 performance-patch scope: AI/lifecycle, GeckoLib bone lookup, renderer/resource reuse, glow/saddle/resource handling, particle scratch reuse, pathfinder scratch reuse, and client lookup reuse.
- Adds the optional client-only Variant Loader 2.7.0 `PassengerLayer.renderPassenger` guard. Empty passenger lists return before passenger rendering work; non-empty passenger paths remain unchanged.
- Keeps the 13 overlapping Variant Loader model-resource Mixins disabled when Variant Loader is present, so Variant Loader remains authoritative for dynamic resource selection.
- Adds exact Variant Loader metadata/version and physical-client gating; absent, malformed, mismatched, or server-side conditions fail closed.
- Fixes the earlier 1.3.1 Mixin validation issue by keeping Mixin helper fields private.
- No Deadlock Fix behavior, gameplay, AI cadence, combat, RNG, networking, worldgen, or Variant Loader implementation changes are included.

## 1.2 → 1.3

- Adds per-instance particle-render `Vector3f` corner reuse and camera-position lookup reuse for the seven Isle of Berk particle families, without replacing `render`.
- Adds `FlyNodeEvaluator` neighbor `EnumMap` reuse and skips the redundant second `MutableBlockPos.set`.
- Adds client packet-handler lookup reuse for Minecraft/entity/particle-option lookups and tame-particle `getRandom()`.
- Keeps original call order, return values, RNG consumption, and client/common side separation on every implemented path.
- Remaining historical families that need method overwrite, combat/target/cadence/network/worldgen changes, or unsafe mutable aliasing stay deferred.

## 1.1 → 1.2

- Attempt FPS Improvment With multiple dragons in view.
- Adds bounded static `ResourceLocation` reuse for the remaining Deadly Nadder, Gronckle, Light Fury, Monstrous Nightmare, Night Fury, Night Light, Skrill, Speed Stinger, Speed Stinger Leader, Stinger, Terrible Terror, Triple Stryke, and Zippleback model families.
- Adds bounded static glow-resource reuse for Night Fury and Light Fury glow layers.
- Adds per-layer saddle-resource reuse without replacing the layer render method.
- Keeps the original variant/titan-wing selection logic authoritative; the cache is consulted only after a path is selected.
- Preserves dynamic and unknown-resource fallback behavior.
- Includes a deterministic fixture for known-path reuse and fallback preservation.

## Remaining historical work deliberately deferred

The old full Vonix edition documented additional renderer-layer, rider/held-item, projectile-explosion, dragon-base, species, AI/combat, math-overwrite, and worldgen changes. They remain excluded from this companion where a narrow, activation-verified Mixin would require broad upstream method replacement or could alter hidden state, timing, RNG, combat, networking, or worldgen semantics. No historical allocation estimate or FPS/RAM/MSPT percentage is claimed as a current measurement.

## Scope boundary

- No Deadlock Fix behavior, chunk-generation guards, or Variant Loader changes.
- No changes to combat rules, target selection, network protocol, or dynamic model selection.
- No universal FPS, MSPT, RAM, or gameplay percentage is promised; results vary by workload, entity count, render distance, particles, shaders, and hardware.

## Installation

1. Install Forge 40.3.x for Minecraft 1.18.2.
2. Install the original Isle of Berk 1.2.0 JAR.
3. Install GeckoLib Forge 3.0.57.
4. Install `isleof-berk-performance-patches-1.3.4.jar`.
5. If using Variant Loader, install it separately; do not also install the standalone render-companion JAR because this combined artifact already contains the exact-gated passenger guard.
6. If you need deadlock protection, install the separately released Deadlock Fix mod as its own companion.
7. Install the same performance-patch version on both client and server for multiplayer.

## Configuration

The generated `config/isleofberkperformance.toml` controls the inherited cadence options:

- `ai_move_throttling_enabled`
- `ai_move_interval_ticks`
- `egg_hatch_check_interval_ticks`
- `shock_particle_interval_ticks`

Changing cadence values intentionally changes timing or visual density; lower intervals perform more work.

## License and provenance

The Vonix-owned performance-companion source and metadata are MIT licensed. Isle of Berk, GeckoLib, and the separate Deadlock Fix mod remain separate dependencies. This repository does not redistribute those dependency JARs.
