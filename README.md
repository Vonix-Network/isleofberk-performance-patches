# Isle of Berk Performance Patches

Release candidate: `0.1.2`.

**Standalone Forge companion / performance-patch mod** for **Isle of Berk 1.2.0**.  
This is **not** a fork, reupload, or replacement of Isle of Berk.

## Required install layout

1. **Keep the original `isleofberk-1.2.0.jar` installed.** This patch does not ship IoB classes or resources and will not load without the real mod.
2. Install **this** JAR (`isleof-berk-performance-patches-*.jar`) alongside it.
3. Optionally install the separate **`isleofberkdeadlockfix`** mod if you want that fix. It is a different artifact; do not merge jars and do not assume one validates the other.

Project independence from `isleofberk-deadlockfix`:

- does not include the `ADragonBase.isWaterBelow()` world-generation guard;
- does not include the Variant Loader adapter;
- does not modify world-generation or spawn-rule behavior.

The client renderer batch reuses the exact texture argument already supplied to `getRenderType` and returns the same `RenderType.entityCutoutNoCull(texture)` layer.

## Current scope

`IOBLookAtPlayerGoal` and `DragonBreedGoal` both perform nearby-entity searches during goal activation. The performance patch leaves owner/target selection, goal continuation, navigation, breeding execution, and render behavior unchanged. It only prevents those activation scans from running on every eligible goal-selector pass.

When a scan tick is skipped, cached `lookAt` / `partner` references are cleared so a stale target cannot keep the goal active.

Server config: `config/isleofberkperformance-server.toml`

```toml
# Safe range: 1..200 inclusive.
# 1 preserves upstream cadence (every eligible canUse pass).
# Default 1 preserves upstream cadence. Values above 1 are opt-in tuning;
# UUID phase staggering spreads work across ticks.
lookAtScanInterval = 1
breedScanInterval = 1
```

Set either value above `1` only as explicit tuning. Higher values change AI activation timing and shared RNG consumption; they are not universal guarantees. Measure the target workload before deployment.

The client renderer patch applies only to the twelve dragon renderer `getRenderType` methods that redundantly resolve a texture already supplied by GeckoLib. It does not alter texture selection or render layers.

```text
The twelve client renderer mixins target only the exact Forge 40.3.0 / IoB 1.2.0 renderer descriptors. A missing target or changed invocation fails the required mixin injection instead of silently degrading.
```


| Component | Supported target |
|---|---|
| Minecraft | 1.18.2 |
| Forge | exact lane `[40.3.0,40.3.1)`; verified with 40.3.0 |
| Isle of Berk | **exactly 1.2.0** (`[1.2.0,1.2.0.1)`); original JAR required |
| GeckoLib | 3.0.57 validation dependency (`[3.0.57,3.0.58)`) |
| Java | 17 |
| Side | Common AI/config plus client-only renderer mixins |

The Modrinth project record currently exposes Isle of Berk 1.18.2 with latest upstream version `1.2.0`; no newer replacement was found during this audit. Re-check the upstream project before release. If a newer IoB version becomes the deployment target, this artifact is blocked until its bytecode and behavior are re-audited.

## Build, fixture, and package audit

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 \
  PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:$PATH \
  ./gradlew clean build --no-daemon --rerun-tasks
./gradlew cadenceFixture --no-daemon
```

`build` runs `auditPackagedJar`, which fails if the primary JAR contains:

- any `com/GACMD/isleofberk/**` implementation classes or resources;
- any `isleofberkdeadlockfix` markers (classes, mixin config, refmap, or mod id strings).

The release candidate must additionally pass exact packaged-JAR runtime testing, controlled before/after measurements, and independent review. Compilation or server readiness alone is not a performance claim.

## Relationship to the deadlock fix

Install this only as a separately reviewed performance candidate. Keep original `isleofberk-1.2.0.jar`. Optionally install `isleofberkdeadlockfix` as its own mod. Do not merge the two jars or assume one validates the other.
