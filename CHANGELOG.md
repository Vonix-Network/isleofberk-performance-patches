# Changelog

## 0.3.0

- Added Forge COMMON registration for `config/isleofberkperformance.toml`.
- Added documented controls for AI movement requests, egg hatch checks, and shock particle cadence.
- Added narrow AI movement mixins for `DragonFlyAndAttackAirbourneTargetGoal`, `UntamedDragonCircleFlightGoal`, and `DragonFollowPlayerFlying`; each uses a `@Unique` per-goal counter and resets at the exact goal lifecycle boundaries available in the original bytecode.
- Added narrow configurable cadence mixins for `ADragonEggBase.tick` and `ShockEffect.applyEffectTick`.
- Preserved shock damage cadence at 20 ticks; only the particle interval is configurable.
- Kept client packet handling deferred: exact handler-side local caching is client-only and pure, but this candidate does not add an unnecessary handler mixin while the packet path remains protocol-owned.
- Retained the prior twelve renderer argument-reuse and fixed-resource model/egg/projectile mixins.

## 0.2.0

- Added twelve exact client renderer transformations and fixed-resource model transformations.
