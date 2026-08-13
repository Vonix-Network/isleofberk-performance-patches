# Isle of Berk Performance Patches — 0.3.0 release evidence report

Evidence inputs were the original `libs/isleofberk-1.2.0.jar`, fork source/history `/root/DEV/isleofberk-performance-fork`, and `/tmp/IsleOfBerk-Vonix-history`.

## Implemented surface

| Family | Disposition | Boundary |
|---|---|---|
| Renderer argument reuse | IMPLEMENTED (prior wave) | 12 exact client renderer bodies; resolved texture argument reused. |
| Fixed model/egg/projectile resources | IMPLEMENTED (prior wave) | 36 exact constructor-only resource methods; dynamic resources remain untouched. |
| AI movement request cadence | IMPLEMENTED (candidate wave) | Three exact flight/follow goals. `@Unique moveTick` counters are reset at goal activation/stop boundaries where present; only navigation/circle calls are gated. `ai_move_throttling_enabled=false` or interval 1 preserves upstream every-eligible-pass behavior. |
| Egg hatch check cadence | IMPLEMENTED (candidate wave) | `ADragonEggBase.tick()V` exact upstream 20-tick constant is redirected to the common config interval; original state updates, hatch side effects, and order remain intact. |
| Shock particle cadence | IMPLEMENTED (candidate wave) | `ShockEffect.applyEffectTick` exact particle 8-tick constant is redirected; damage's separate 20-tick constant is untouched. |
| Packet particle handler local caching | DEFERRED | Fork proves a client-only pure-cache optimization, but no packet protocol or handler behavior was added to this candidate without a stronger runtime-side proof. |

## Config defaults (`config/isleofberkperformance.toml`)

- `ai_move_throttling_enabled = true` — upstream/normal false; optimized default true.
- `ai_move_interval_ticks = 4` — upstream/normal 1; 1 disables cadence optimization.
- `egg_hatch_check_interval_ticks = 20` — pinned IoB 1.2.0 upstream/normal 20; optimized default 20. Lower values increase checks and alter timing granularity.
- `shock_particle_interval_ticks = 8` — pinned IoB 1.2.0 upstream/normal 8; optimized default 8. Lower values increase packet and visual frequency.
- Shock damage remains fixed at 20 ticks.

Forge COMMON registration is in `IsleOfBerkPerformance`; Forge-generated comments explain timing/visual tradeoffs and the pinned upstream baselines.

## Every audited Vonix wave

- `vonix.1`: DEFERRED — cadence/config-cache and defensive changes were not copied wholesale.
- `vonix.2`: DEFERRED — deadlock/chunk access excluded.
- `vonix.3`: DEFERRED — variant registration/taming UX.
- `vonix.4`: DEFERRED — spawn baseline/routing/taming.
- `vonix.5`: PARTIAL — fixed resources/renderers implemented; exact flight movement cadence now implemented through configurable narrow mixins; unrelated deadlock/scratch changes deferred.
- `vonix.6`: PARTIAL — safe exact cadence surfaces implemented; spawn/deadlock and broad method-local rewrites deferred.
- `vonix.7`: DEFERRED — pathfinder structure substitution.
- `vonix.8`: DEFERRED — projectile ray-march scratch/predicate lifetime.
- `vonix.9`: PARTIAL — exact configurable AI movement request surfaces implemented; target/combat/navigation substitutions deferred.
- `vonix.10`: PARTIAL — exact egg/shock cadence surfaces implemented; entity tick/state/combat rewrites deferred.
- `vonix.11`: PARTIAL — renderer and fixed resources prior wave; dynamic layers, particles, and egg timing addressed only where exact constants were proven.
- `vonix.12`: DEFERRED/PARTIAL — no protocol changes; packet handler caching deferred.
- `vonix.13`: PARTIAL — fixed resources plus ShockEffect cadence; particle render/event/worldgen/item/effect changes outside the exact narrow boundary deferred.

## Explicit exclusions / unresolved gaps

No `distanceToSqr` substitutions, deadlock/getChunkNow, network protocol changes, spawn/worldgen, particle static shared arrays, pathfinder algorithm substitutions, original IoB classes, or deadlock-fix classes/resources were added. Client packet local caching remains deferred. Acceptance evidence is recorded in the release process and the pinned artifact hash.
