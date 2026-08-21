# Isle of Berk Performance Patches 1.3

A standalone Forge Mixin companion for **Isle of Berk 1.2.0** on **Minecraft 1.18.2**.

> This is a performance companion, not a fork or replacement. Install the original `isleofberk-1.2.0.jar` and GeckoLib 3.0.57 separately. The Deadlock Fix mod is a separate companion and is not included here. This JAR does not redistribute Isle of Berk classes or resources.

V1.3 is a successor candidate and a targeted safe port of remaining historical Vonix optimizations that fit narrow Mixins. It is not a complete Vonix port. No FPS, RAM, or MSPT percentage and no guaranteed performance are claimed.

## Compatibility

- Minecraft 1.18.2
- Forge 40.3.x; compiled against 40.3.0
- Isle of Berk exactly 1.2.0
- GeckoLib 3.0.57
- Java 17 target; the V1.3 build and verification gates run under JDK 17

Install the same V1.3 performance-patch JAR on both client and server for multiplayer. Client rendering mixins are client-only; common performance mixins load on both sides. Install it on the client for the render-resource changes to affect client rendering.

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
4. Install `isleof-berk-performance-patches-1.3.jar`.
5. If you need deadlock protection, install the separately released Deadlock Fix mod as its own companion.
6. Install the same performance-patch version on both client and server for multiplayer.

## Configuration

The generated `config/isleofberkperformance.toml` controls the inherited cadence options:

- `ai_move_throttling_enabled`
- `ai_move_interval_ticks`
- `egg_hatch_check_interval_ticks`
- `shock_particle_interval_ticks`

Changing cadence values intentionally changes timing or visual density; lower intervals perform more work.

## License and provenance

The Vonix-owned performance-companion source and metadata are MIT licensed. Isle of Berk, GeckoLib, and the separate Deadlock Fix mod remain separate dependencies. This repository does not redistribute those dependency JARs.
