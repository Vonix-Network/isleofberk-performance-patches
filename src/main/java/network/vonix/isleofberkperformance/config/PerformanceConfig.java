package network.vonix.isleofberkperformance.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Server config for the two throttled IoB AI activation scans.
 * Bounds come from {@link CadencePolicy} so misconfiguration cannot set zero/negative intervals
 * or extreme multi-minute freezes.
 */
public final class PerformanceConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue lookAtScanInterval;
    public static final ForgeConfigSpec.IntValue breedScanInterval;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment(
                "Isle of Berk Performance Patches (companion, not a fork).",
                "Controls only nearby-entity scan cadence for two AI goals.",
                "1 = preserve upstream cadence on every eligible canUse pass.",
                "Values >1 throttle scans; UUID phase staggering spreads work across ticks.",
                "Safe range: " + CadencePolicy.INTERVAL_MIN + ".." + CadencePolicy.INTERVAL_MAX + " ticks inclusive."
        );
        lookAtScanInterval = builder.comment(
                        "Ticks between IOBLookAtPlayerGoal nearby-player activation scans.",
                        "1 preserves upstream cadence. Values above 1 are opt-in tuning. Default " + CadencePolicy.INTERVAL_DEFAULT + ".",
                        "On skipped ticks lookAt is cleared so a stale target cannot keep the goal active."
                )
                .defineInRange(
                        "lookAtScanInterval",
                        CadencePolicy.INTERVAL_DEFAULT,
                        CadencePolicy.INTERVAL_MIN,
                        CadencePolicy.INTERVAL_MAX
                );
        breedScanInterval = builder.comment(
                        "Ticks between DragonBreedGoal nearby-partner activation scans.",
                        "1 preserves upstream cadence. Values above 1 are opt-in tuning. Default " + CadencePolicy.INTERVAL_DEFAULT + ".",
                        "On skipped ticks partner is cleared so a stale partner cannot keep the goal active."
                )
                .defineInRange(
                        "breedScanInterval",
                        CadencePolicy.INTERVAL_DEFAULT,
                        CadencePolicy.INTERVAL_MIN,
                        CadencePolicy.INTERVAL_MAX
                );
        SPEC = builder.build();
    }

    private PerformanceConfig() {}
}
