# Changelog

## 1.1.0 — 1.0 → 1.1

### Added

- Added a guarded GeckoLib dragon-bone lookup index for Isle of Berk dragon models. The index validates the live bone list and falls back to GeckoLib's original lookup after list mutation or duplicate-name changes.

### Retained from 1.0

- Configurable flight/follow AI movement-request throttling.
- Configurable egg hatch-check cadence.
- Configurable ShockEffect particle cadence; ShockEffect damage remains on its original cadence.
- Narrow client renderer argument reuse.
- Fixed-resource egg-animation and projectile transformations.
- Variant-compatible dynamic dragon and egg model/resource selection.

### Compatibility and scope

- Minecraft 1.18.2, Forge 40.3.x, Java 17.
- Isle of Berk 1.2.0 and GeckoLib 3.0.57 remain required separate dependencies.
- The original Isle of Berk JAR remains required. This release does not replace or redistribute Isle of Berk.
- The Isle of Berk Deadlock Fix remains a separate companion mod and is not included in this JAR.
- No changes to combat rules, damage cadence, target selection, network protocol, or dynamic model selection.
- No universal FPS, MSPT, RAM, or gameplay percentage is promised; results vary by workload.
