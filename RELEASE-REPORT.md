# Isle of Berk Performance Patches 1.0.1 release report

Stable GitHub release for the standalone Forge companion patch.

## Compatibility cell

- Minecraft: `1.18.2`
- Forge compile/runtime lane: `40.3.0` compile pin; declared compatibility range `[40.3.0,40.4.0)`
- Java: `17`
- Isle of Berk dependency: exactly `1.2.0`
- GeckoLib dependency: `3.0.57`
- Companion mod ID: `isleofberkperformance`
- Release tag: `v1.0.1`

## Artifact identity

- File: `isleof-berk-performance-patches-1.0.1.jar`
- Size: `37550 bytes`
- SHA-256: `df44f3788408f903df9e86dc25248220f1089504261e46a7190dcb3e8845f20a`
- Predecessor artifact: `isleof-berk-performance-patches-1.0.0.jar`
- Predecessor SHA-256: `15df9183a49e4d83a9d5e0583cec61a5a90549d873e5a64bb0116401988667e2`
- Source identity: the commit targeted by tag `v1.0.1`; verified after the final commit

## Included scope

- AI cadence implementation: `AiMoveCadence` with exact goal lifecycle reset and safe re-arm on settings changes.
- Atomic primitive settings implementation: `PerformanceSettings` snapshots for hot-path reads.
- P01 inactive-follow-goal `tailingDragons` cleanup without replacing active formation state.
- Twelve narrow client renderer argument-reuse mixins.
- Eight fixed-resource egg-animation/projectile transformations.
- Variant Loader-compatible dynamic model, texture, animation, and layer selection.
- Companion-only packaging: Isle of Berk, Deadlock Fix, and Threaded Horizons implementation classes/resources are not bundled.

## Deliberately deferred scope

- Large protected scan-body rewrites in `ADragonBase` and `ADragonBaseFlyingRideable`.
- Heightmap/navigation allocation removal from the large follow-goal method.
- Projectile collision/explosion/deletion/damage/block-griefing changes.
- Dragon flap packet decode/handler allocation and scheduling changes.
- Deadlock, chunk-access safety, networking, combat, RNG, progression, spawning, world generation, and dynamic variant-resource behavior changes.

## Matched RSS smoke observation

This is a bounded boot-RSS observation, not a universal RAM, MSPT, FPS, or gameplay-performance claim.

- Protocol: two runs per variant; same disposable Forge 1.18.2/40.3.12 runtime stack, Java 17.0.19, `-Xms2G -Xmx4G`, fresh world per run, exact JAR replacement, `Done` gate, and six RSS samples from 0–16 seconds after `Done` before the stop command.
- Baseline: `1.0.0`, SHA-256 `15df9183a49e4d83a9d5e0583cec61a5a90549d873e5a64bb0116401988667e2`; two-run mean `4,198,330 KB`.
- Stable: `1.0.1`, SHA-256 `df44f3788408f903df9e86dc25248220f1089504261e46a7190dcb3e8845f20a`; two-run mean `2,842,066 KB`.
- Observed mean RSS difference: `32.3%` lower for `1.0.1`; pairwise run comparisons ranged from `21.0%` to `42.1%` lower.
- Interpretation: the requested nearly-50% RAM result was not independently reproduced by this bounded smoke protocol. A real user-workload claim still requires matched entity population, workload, warm-up, duration, JVM/hardware, and profiler methodology.

## Packaged runtime evidence

- Runtime: fresh disposable Forge `1.18.2-40.3.12` server, Java `17.0.19`, no source-set injection, fresh world.
- Mods staged: exactly Isle of Berk `1.2.0`, GeckoLib Forge `3.0.57`, Variant Loader `2.6.4`, and this stable companion JAR.
- Launch log preflight marker: `isleof-berk-performance-patches-1.0.1.jar`, `37550` bytes, SHA-256 `df44f3788408f903df9e86dc25248220f1089504261e46a7190dcb3e8845f20a`.
- Runtime log: `/root/work/iob-minimal-runtime-1.0.1-20260820/logs/release-launch.log`; SHA-256 `92088e11971d44c0b10931592649b69bd8c3db79eef74dc10d4e5e41813dfd68`; size `41369` bytes.
- Observed: stable companion loaded, `Done` reached, clean stop, zero Mixin application errors, linkage errors, `ERROR`, `FATAL`, or uncaught exception markers.
- Limitation: this is a dedicated-server packaged boot gate; no client visual/user-play-path gate is asserted.

## Verification

Command:

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
./gradlew clean check build --offline --no-daemon --rerun-tasks
```

Required gates: clean Java 17 build/tests, mapping and bytecode fixtures, cadence/lifecycle/snapshot fixtures, package and companion-only audits, provenance identity, and the historical implementation-scope fixture.

The final release record must retain the exact artifact size and SHA-256 above. Packaged Forge runtime evidence is recorded separately from source-set build evidence. No universal RAM/MSPT/FPS percentage is asserted by this release; workload-specific performance depends on world state, entity population, JVM, render settings, and configuration.

## Provenance

The original Isle of Berk JAR remains a required separately obtained dependency and is not redistributed by this project. The release source and point-in-time history are available from the `v1.0.1` tag and GitHub source archive.
