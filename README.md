# Isle of Berk Performance Patches 1.1

A standalone Forge Mixin companion for **Isle of Berk 1.2.0** on **Minecraft 1.18.2**.

> This is a performance companion, not a fork or replacement. Install the original `isleofberk-1.2.0.jar` and GeckoLib 3.0.57 separately. The deadlock-fix mod is a separate companion and is not included here. This JAR does not redistribute Isle of Berk classes or resources.

## Compatibility

- Minecraft 1.18.2
- Forge 40.3.x; compiled against 40.3.0
- Isle of Berk exactly 1.2.0
- GeckoLib 3.0.57
- Java 17

Install the same 1.1.0 performance-patch JAR on both client and server for multiplayer. Client rendering mixins are client-only; common performance mixins load on both sides.

## 1.0 → 1.1

Version 1.1 combines the verified 1.0 performance work with the guarded GeckoLib dragon-bone lookup improvement:

- Adds a validated GeckoLib dragon-bone index for Isle of Berk dragon models, with mutation detection and fallback to GeckoLib's original lookup when the live list changes.
- Retains configurable flight/follow movement-request throttling, egg hatch-check cadence, and ShockEffect particle cadence.
- Retains the narrow renderer argument-reuse and fixed-resource transformations from 1.0.
- Keeps ShockEffect damage cadence, combat rules, target selection, network protocol, dynamic model selection, and non-targeted Isle of Berk behavior unchanged.
- Does not include the separate Isle of Berk Deadlock Fix mod or its world-generation/Variant Loader guards.

No universal FPS, MSPT, RAM, or gameplay-improvement percentage is promised. Results vary with the installed pack, entity count, and renderer workload.

## Installation

1. Install Forge 40.3.x for Minecraft 1.18.2.
2. Install the original Isle of Berk 1.2.0 JAR.
3. Install GeckoLib Forge 3.0.57.
4. Install `isleof-berk-performance-patches-1.1.0.jar`.
5. If you need deadlock protection, install the separately released Deadlock Fix mod as its own companion.
6. Install the same performance-patch version on both client and server for multiplayer.

## Configuration

The generated `config/isleofberkperformance.toml` controls the cadence options:

- `ai_move_throttling_enabled`
- `ai_move_interval_ticks`
- `egg_hatch_check_interval_ticks`
- `shock_particle_interval_ticks`

The default values are documented in the generated config. Changing cadence values intentionally changes timing or visual density; lower intervals perform more work.

## License and provenance

The Vonix-owned performance-companion source and metadata are MIT licensed. Isle of Berk, GeckoLib, and the separate Deadlock Fix mod remain separate dependencies. This repository does not redistribute those dependency JARs.
