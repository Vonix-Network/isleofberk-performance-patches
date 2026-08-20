# Successor candidate report — 1.0.1-rc.1 (not accepted)

Unpublished prerelease successor on base HEAD `515b9a677a39f72e9be12330355cbf765dbafcea`.
Distinct from published `1.0.0` (`SHA256 15df9183a49e4d83a9d5e0583cec61a5a90549d873e5a64bb0116401988667e2`).

## Artifact identity

- Version coordinate: `1.0.1-rc.1`
- Artifact path: `build/libs/isleof-berk-performance-patches-1.0.1-rc.1.jar`
- Artifact filename: `isleof-berk-performance-patches-1.0.1-rc.1.jar`
- Size: 37557 bytes
- SHA-256: `57b880f636fff7d8c1f69b310465ac13964817812586651a5531142043ab5577`
- Predecessor 1.0.0 path: `release-download/isleof-berk-performance-patches-1.0.0.jar`
- Predecessor 1.0.0 SHA-256: `15df9183a49e4d83a9d5e0583cec61a5a90549d873e5a64bb0116401988667e2`
- Dirty tree: yes (uncommitted SOL-001..006 + IDENTITY-001); no git commit by this worker

## Class inventory (selected)

- `AiMoveCadence` present
- `PerformanceSettings` present
- Full class list: `evidence/successor-jar-classes.txt`

## Cadence contract

- One allow/deny decision at each goal `tick()` HEAD from one atomic AI snapshot
- First eligible request after WrappedGoal start/stop reset runs immediately
- Later due ticks use `ai_move_interval_ticks`; all intended calls on a due tick may run
- Egg/shock hot paths read `PerformanceSettings` primitives (no mixin `ForgeConfigSpec.get()`)

## Fixed-resource surface

- Twelve client renderer mixins
- Eight fixed-resource model methods (Variant Loader retains dragon/egg dynamic resources)

## Provenance

- Hashed build inputs + dependency digests: `evidence/candidate-manifest.json`
- Two clean offline `check build` byte-identity recorded in `evidence/grok-final-identity-report.json`

## Unresolved

- SOL-007 client/server runtime multi-mod launch evidence still unresolved
- Not accepted; not published
