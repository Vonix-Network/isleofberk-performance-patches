# 1.2 Remaining Scope Audit

- Historical comparison: `v1.2.1-vonix.5..v1.2.1-vonix.13` in `/root/work/iob-vonix-source-1.2.1-vonix.13-20260821`.
- Historical changed paths inspected: **97**.
- Current candidate keeps the original Isle of Berk implementation external.

## Implemented or inherited

- **R12-model-resource-cache** — `IMPLEMENTED`: 16 client mixins cover all 13 dragon model families, both glow layers, and BaseSaddleAndChestsLayer; fixed paths are bounded and dynamic fallback remains.
- **R11-renderer-texture-reuse** — `INHERITED_1_1`: The accepted 1.1 companion already contains the 12 renderer texture-parameter reuse mixins.
- **R1-original-dependency** — `REQUIRED`: Original Isle of Berk 1.2.0 and GeckoLib 3.0.57 remain external dependencies.
- **R2-deadlock-separation** — `PASS`: No deadlock-fix package, mixin, class, or resource is included.

## Deferred historical families

- **deferred-other-historical-wave** — 22 paths. Not ported as a companion because the safe narrow Mixin boundary is not established; broad overwrite/copy of upstream implementation or hidden timing/state risk is disallowed.
- **deferred-wave-11-egg-tick-and-position-cache** — 4 paths. Not ported as a companion because the safe narrow Mixin boundary is not established; broad overwrite/copy of upstream implementation or hidden timing/state risk is disallowed.
- **deferred-wave-11-layer-state-caching** — 2 paths. Not ported as a companion because the safe narrow Mixin boundary is not established; broad overwrite/copy of upstream implementation or hidden timing/state risk is disallowed.
- **deferred-wave-12-client-packet-handlers** — 4 paths. Not ported as a companion because the safe narrow Mixin boundary is not established; broad overwrite/copy of upstream implementation or hidden timing/state risk is disallowed.
- **deferred-wave-12-math-and-interpolation** — 4 paths. Not ported as a companion because the safe narrow Mixin boundary is not established; broad overwrite/copy of upstream implementation or hidden timing/state risk is disallowed.
- **deferred-wave-13-particle-render-hot-path** — 7 paths. Not ported as a companion because the safe narrow Mixin boundary is not established; broad overwrite/copy of upstream implementation or hidden timing/state risk is disallowed.
- **deferred-wave-13-residual-effects-items-events** — 5 paths. Not ported as a companion because the safe narrow Mixin boundary is not established; broad overwrite/copy of upstream implementation or hidden timing/state risk is disallowed.
- **deferred-wave-6-and-10-dragon-base-hot-path** — 4 paths. Not ported as a companion because the safe narrow Mixin boundary is not established; broad overwrite/copy of upstream implementation or hidden timing/state risk is disallowed.
- **deferred-wave-6-and-10-species-hot-path** — 14 paths. Not ported as a companion because the safe narrow Mixin boundary is not established; broad overwrite/copy of upstream implementation or hidden timing/state risk is disallowed.
- **deferred-wave-7-pathfinder-allocation** — 1 paths. Not ported as a companion because the safe narrow Mixin boundary is not established; broad overwrite/copy of upstream implementation or hidden timing/state risk is disallowed.
- **deferred-wave-8-projectile-hot-path** — 4 paths. Not ported as a companion because the safe narrow Mixin boundary is not established; broad overwrite/copy of upstream implementation or hidden timing/state risk is disallowed.
- **deferred-wave-9-ai-and-navigation-hot-path** — 5 paths. Not ported as a companion because the safe narrow Mixin boundary is not established; broad overwrite/copy of upstream implementation or hidden timing/state risk is disallowed.

## Boundary

No FPS/RAM/MSPT percentage is claimed; historical allocation estimates are not current measurements.
