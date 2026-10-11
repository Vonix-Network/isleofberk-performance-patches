# HISTORICAL ARCHIVE — 0.3.1-rc.1 evidence (not the current dirty successor candidate)

This file is retained as historical provenance only. It does not describe the current AiMoveCadence/PerformanceSettings successor tree or its artifact.

# 0.3.1-rc.1 release-candidate evidence

This document records the implemented scope, audited exclusions, and release-candidate evidence for the 0.3.1-rc.1 standalone companion. It is not a claim that every historical Vonix fork hunk can be safely reproduced as a separate Mixin companion. The artifact implementation commit — the exact source/build commit for the pinned JAR — is `dcf712be5f88278f2a1fd87b533f88a150465105` (`Harden ZIP extra timestamp handling`). This report is committed as a separate provenance document descendant of the artifact implementation commit; verify the report commit with git log/rev-parse externally. The report commit is not the artifact source commit. Prior independent reviews rejected predecessor trees; this unpublished candidate requires a fresh independent Sol review. GitHub publication remains pending and is not authorized by this document.

## Release identity

- Candidate version: `0.3.1-rc.1` (unpublished successor to immutable [v0.3.0](https://github.com/Vonix-Network/isleofberk-performance-patches/releases/tag/v0.3.0))
- Artifact implementation commit: `dcf712be5f88278f2a1fd87b533f88a150465105` (`Harden ZIP extra timestamp handling`) — exact source/build commit for the pinned JAR
- Evidence/report commit: this report is committed as a separate provenance document descendant of the artifact implementation commit; verify the report commit with git log/rev-parse externally. The report commit is not the artifact source commit and is not embedded here because a commit cannot contain its own full hash.
- Artifact: `isleof-berk-performance-patches-0.3.1-rc.1.jar`
- Size: 47,478 bytes
- SHA-256: `7238b74e13167a59310cd1e9e14ff56048733a8755a272602a0aa6b0064a38c9`
- Target: Minecraft 1.18.2, Forge 40.3.0, Java 17, Isle of Berk 1.2.0, GeckoLib 3.0.57
- Packaging: `reobfJar` is unchanged; `canonicalizeReobfJar` then rewrites only ZIP timestamps and timestamp extra fields so clean builds are byte-identical. Entry contents, order, manifest, refmap, and bytecode are not modified.

## Implemented surface

| Family | Status | Boundary |
|---|---|---|
| Renderer argument reuse | Implemented | Twelve exact client renderer bodies; resolved texture arguments are reused. |
| Fixed model/egg/projectile resources | Implemented | Thirty-six exact constructor-only resource methods; dynamic resources remain untouched. |
| AI movement-request cadence | Implemented | Three exact flight/follow goals; `@Unique` counters reset by the common `WrappedGoal` start/stop transition hook. Target `canUse()`/`start()`/`stop()` methods remain untouched. Vanilla `canContinueToUse()` delegates to `canUse()`, so `canUse()` is not a reset point. The first eligible request after start runs immediately, then every configured interval. Only navigation/circle calls are gated. |
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

## Candidate evidence

- Java 17 Gradle clean `check build --offline --no-daemon --rerun-tasks`: `BUILD SUCCESSFUL` twice from the same tree; the two JARs are byte-identical (`sha256sum` and `cmp`).
- `auditPackagedJar` and `mappingFixture` inspect the canonical post-`reobfJar` artifact.
- `configFixture`: required COMMON keys, defaults, and comments present.
- `RendererBytecodeFixture`: 12 renderers and 36 fixed-resource methods passed.
- `infoZipUxExtraFixture`: deterministic synthetic extra fields for Info-ZIP `0x5455` UT, NTFS `0x000a`, and Info-ZIP `0x5855` UX; only selected timestamp bytes change; flags, reserved prefix, UID/GID, other attributes, and trailing/unknown bytes are preserved; malformed, truncated, zero-length, and short timestamp layouts fail closed.
- Prior production/SRG Forge runtime evidence still applies to the unchanged companion bytecode/entry contents (six common mixins applied; server reached `Done` without `MixinApplyError`, `InvalidInjection`, `InvalidInjectionException`, or `AbstractMethodError`). That run is not a hash pin for this reproducible artifact. Runtime evidence is limited to the unmodified `/root/iob-0.3.1-rc.1-production-gate-repair2-final/logs/latest.log` and `/root/iob-0.3.1-rc.1-production-gate-repair2-final/logs/debug.log`. `/tmp/iob-grok-0.3.1-rc.1-committed-runtime.log` is disqualified/invalid: it is contaminated by embedded worker prose and is not a complete raw console log. No replacement console log was fabricated.
- Independent Sol review of the reviewed prior candidate / labeled predecessor `037002913f2f0f1892e27a9d6477d2251d9a5677` (`Harden ZIP metadata canonicalization`) plus artifact SHA-256 `7238b74e13167a59310cd1e9e14ff56048733a8755a272602a0aa6b0064a38c9` rejected that predecessor with no P0. Findings were the stale predecessor HEAD and runtime paths in this document, unsupported acceptance wording in README/CHANGELOG, and incomplete UT/NTFS/UX extra-field canonicalization. That REJECT is not a review of this exact current candidate. This exact current candidate (artifact implementation commit `dcf712be5f88278f2a1fd87b533f88a150465105`, `Harden ZIP extra timestamp handling`) is unpublished, has not been accepted, and requires a fresh independent Sol review. This report is committed as a separate provenance document descendant of the artifact implementation commit; verify the report commit with git log/rev-parse externally. The report commit is not the artifact source commit. Publication is pending and is not authorized.
- GitHub prerelease/publication: pending. Do not treat this document as a published release.

### Versioned final-gate evidence

- Candidate artifact: `build/libs/isleof-berk-performance-patches-0.3.1-rc.1.jar`, 47,478 bytes, SHA-256 `7238b74e13167a59310cd1e9e14ff56048733a8755a272602a0aa6b0064a38c9` after `canonicalizeReobfJar`.
- Two clean offline builds from the same tree matched this hash. Durable hash/`cmp` evidence is retained in `/tmp/iob-0.3.1-rc.1-final-evidence.txt` because the referenced build logs (`/tmp/iob-parent-repro-a.log`, `/tmp/iob-parent-repro-b.log`) record only `BUILD SUCCESSFUL` and do not themselves print digests.
- Independent Sol REJECT of the reviewed prior candidate / labeled predecessor (`037002913f2f0f1892e27a9d6477d2251d9a5677`, `Harden ZIP metadata canonicalization`, SHA-256 `7238b74e13167a59310cd1e9e14ff56048733a8755a272602a0aa6b0064a38c9`) had no P0. That REJECT is not a review of this exact current candidate. This exact current candidate (artifact implementation commit `dcf712be5f88278f2a1fd87b533f88a150465105`, `Harden ZIP extra timestamp handling`) is unpublished, has not been accepted, and requires a fresh independent Sol review. This report is committed as a separate provenance document descendant of the artifact implementation commit; verify the report commit with git log/rev-parse externally. The report commit is not the artifact source commit. Publication is pending and is not authorized by this document.

### Sol P2/P3 hygiene disposition

- P2 reproducibility evidence: resolved for parent review by retaining both clean-build log paths, both hash lines, retained byte-identical copies, and `cmp PASS` in `/tmp/iob-0.3.1-rc.1-final-evidence.txt`. The logs themselves were not rewritten.
- P2 provenance: the artifact implementation commit — the exact source/build commit for the pinned JAR — is `dcf712be5f88278f2a1fd87b533f88a150465105` (`Harden ZIP extra timestamp handling`). This report is committed as a separate provenance document descendant of the artifact implementation commit; verify the report commit with git log/rev-parse externally. The report commit is not the artifact source commit. The labeled predecessor is `037002913f2f0f1892e27a9d6477d2251d9a5677` (`Harden ZIP metadata canonicalization`). This exact current candidate is unpublished and is not a published identity. A fresh independent Sol review is required. Tag and GitHub publication remain pending and are not authorized by this document. This worker did not commit, tag, publish, or accept.
- P3 contaminated console log: `/tmp/iob-grok-0.3.1-rc.1-committed-runtime.log` is disqualified/invalid and is not used as runtime evidence. Use only the unmodified `/root/iob-0.3.1-rc.1-production-gate-repair2-final/logs/latest.log` and `/root/iob-0.3.1-rc.1-production-gate-repair2-final/logs/debug.log`.
- Stale `config/isleofberkperformance.toml` 0.3.0 reference wording: resolved in-tree. Keys, defaults, format, and runtime values are unchanged.

No quantitative performance claim is made because matched before/after profiling was not performed.
