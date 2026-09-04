# Isle of Berk Performance Patches — Public Status

**Status date:** September 4, 2026
**Last live read-only observation:** September 4, 2026 at 03:39:01 UTC
**Current source line:** 1.3.7 development snapshot
**Latest tagged stable release:** v1.3.4

## Executive status

The public `main` source snapshot now contains the full Isle of Berk Performance Patches companion source through the 1.3.7 development candidate, including the StructureManager cache work and the earlier verified performance-patch families.

The live server is running the 1.3.7 candidate, and its early post-restart memory readings are substantially below the previously observed high-water mark. This is an **early positive signal**, not proof of a resolved memory leak or a measured performance improvement.

1.3.7 remains an unpublished development candidate. No CurseForge upload, GitHub release/tag, live deployment action, or additional restart is implied by this source publication.

## Live server observation

The read-only panel snapshot at **2026-09-04 03:39:01 UTC** reported:

- Server: Isle of Berk (Claws of Berk)
- State: `running`
- Uptime: approximately 19.4 minutes
- Memory: approximately **7,340.5 MB of 32,768 MB** (**22.40%**)
- CPU: approximately **90.7%** at the instant of the snapshot
- The 1.3.7 performance-patch JAR was previously confirmed enabled and initialized during the boot sequence.

For context:

- Earlier observed high-water mark: approximately **30,486.4 MB** (**93.04%** of the configured memory limit).
- A pre-restart sample on September 4 was approximately **19,513.3 MB** (**59.55%**).
- The server restarted during the observation window. The restart cause was not established by the read-only evidence and is not attributed to 1.3.7.
- The post-restart sample was taken with fewer players than normal, so it is not a matched workload comparison.

## Readiness and error boundary

The process remained running and memory growth was modest across the short post-restart window. However, the bounded readiness scan did not find a fresh `Done (` marker in the current debug log. The log was still growing and the read path reported a truncated multi-megabyte file, so full post-boot readiness is not independently established by this observation.

Scoped checks found no candidate-specific Mixin failure, `OutOfMemoryError`, or watchdog signature. Existing non-candidate warnings remain in the environment, including the Dragon Fire Nation Soldier attribute warning and compatibility/configuration warnings involving Radium/ServerCore/SnowRealMagic, MixinExtras, and Quark. Those warnings are not represented as fixes in 1.3.7.

## What 1.3.7 contains

1.3.7 carries forward the earlier companion patch families:

- AI movement-request cadence throttling with exact goal lifecycle reset.
- Egg hatch-check and ShockEffect particle cadence controls, with ShockEffect damage cadence preserved.
- GeckoLib dragon-bone lookup reuse with safe fallback after bone-list mutation or duplicate names.
- Fixed-path renderer and resource reuse for supported Isle of Berk client paths.
- Particle scratch/vector reuse and camera-position lookup reuse.
- Pathfinder neighbor-map reuse and a redundant position-write removal.
- Client packet-handler lookup reuse with original call order and RNG consumption preserved.
- Variant Loader resource-overlap and passenger-render compatibility gates.
- Server-only bounded StructureManager parsed-template cache replacement.

The 1.3.7-specific cache setting is:

- `structure_cache_max_entries = 512`
- Accepted configuration range: `64..4096`
- Guava `softValues()` semantics are preserved.
- The bound is read from reload-correct primitive snapshots during `StructureManager` construction.
- The mixin runs at priority `900` so the intended higher-priority ModernFix replacement can occur first.
- The candidate does not change Lootr persistence, UUID/score data, world data, structure definitions, Threaded Horizons, or any third-party JAR.

## Version status

- **v0.1.1, v0.1.2, v0.3.0, v1.0.0, v1.0.1, and v1.3.4:** published historical releases; immutable release records remain on GitHub.
- **1.1.0 through 1.3.3:** documented development/release-line milestones; no separate current stable tag is asserted for these entries.
- **1.3.5:** unpublished StructureManager cache candidate using a bounded soft-value cache with a 64-entry bound; limited disposable evidence only.
- **1.3.6:** unpublished configurable StructureManager cache candidate with a 512-entry default and `64..4096` clamp range; matched production A/B and fresh heap evidence unavailable.
- **1.3.7:** unpublished metadata-successor/development snapshot. Functional artifact bytes were carried forward from the 1.3.6 candidate; the source packet repaired candidate lineage/manifest evidence and updates the public source status.

## Candidate artifact identity

The 1.3.7 candidate artifact recorded in the local evidence packet is:

```text
File:   isleof-berk-performance-patches-1.3.7.jar
Size:   85,892 bytes
SHA256: 389d6576ad09bca8406af889f470903ad6af93a9b4fa0aa3c61a8eb8f7d29acf
```

This hash identifies the candidate artifact; it does not mean that the artifact is a GitHub release asset or that it has passed production acceptance.

## Verification status

Recorded deterministic candidate evidence includes:

- Java 17 Gradle clean `check build`: pass.
- Packaged JAR audit: pass.
- Mixin inventory, mapping, lifecycle, Variant Loader, hot-path, and StructureManager cache fixtures: pass.
- Companion-only packaging boundary: pass.

Still required before describing 1.3.7 as a proven fix or stable release:

- A longer observation window with normal player activity.
- Matched before/after RAM, GC, MSPT, and workload evidence.
- Fresh heap dominator/GC-root evidence if memory retention remains suspected.
- A complete packaged full-pack dedicated-server boot/readiness check for the exact candidate.
- Independent candidate review/acceptance for the final source snapshot if a release or artifact publication is requested.

## Safety boundary

This project remains a companion mod. It does not replace Isle of Berk, edit third-party JARs, alter Lootr persistence, or include the separate Deadlock Fix mod. Source publication does not authorize a server restart, live `/mods` or `/config` mutation, world change, CurseForge upload, or stable release creation.

## Owner-facing conclusion

The current evidence supports **leaving 1.3.7 in observation** rather than reverting solely on the memory data seen so far. The lower memory level is encouraging, but the server had just restarted, player load was low, and readiness/performance evidence is incomplete. The next meaningful decision should be based on a normal-player time window, not the early post-restart number alone.
