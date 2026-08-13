package network.vonix.isleofberkperformance.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Common-side controls for cadence changes that intentionally affect gameplay or visual timing.
 * Forge owns loading/reloading the values from config/isleofberkperformance.toml.
 */
public final class PerformanceConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue AI_MOVE_THROTTLING_ENABLED;
    public static final ForgeConfigSpec.IntValue AI_MOVE_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue EGG_HATCH_CHECK_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue SHOCK_PARTICLE_INTERVAL_TICKS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment(
                "Isle of Berk Performance Patches common configuration.",
                "These controls intentionally change gameplay timing or visual cadence.",
                "For AI movement, set an interval to 1 to disable that cadence optimization and use every eligible request.",
                "Damage cadence is not configurable and remains fixed at 20 ticks."
        );
        builder.push("performance");

        AI_MOVE_THROTTLING_ENABLED = builder
                .comment(
                        "Whether repeated AI navigation movement requests are throttled.",
                        "Upstream/normal behavior: false (no throttle; movement requests may be issued every tick).",
                        "Optimized default: true. Tradeoff: navigation requests are less frequent and movement response can be less immediate."
                )
                .define("ai_move_throttling_enabled", true);
        AI_MOVE_INTERVAL_TICKS = builder
                .comment(
                        "AI movement-request interval in ticks when throttling is enabled.",
                        "Upstream/normal behavior: 1 tick. Optimized default: 4 ticks.",
                        "Tradeoff: larger intervals reduce AI/navigation work but can make following or flight corrections less responsive.",
                        "1 disables cadence optimization for this control."
                )
                .defineInRange("ai_move_interval_ticks", 4, 1, 20);
        EGG_HATCH_CHECK_INTERVAL_TICKS = builder
                .comment(
                        "Server-side egg temperature and hatch-progress check interval in ticks.",
                        "Upstream/normal behavior in Isle of Berk 1.2.0: 20 ticks. Optimized default: 20 ticks.",
                        "Upstream/normal behavior is already 20 ticks; retaining 20 preserves its hatch timing granularity.",
                        "Lower values check more often and can change hatch timing while increasing server work."
                )
                .defineInRange("egg_hatch_check_interval_ticks", 20, 1, 200);
        SHOCK_PARTICLE_INTERVAL_TICKS = builder
                .comment(
                        "Shock-effect particle packet interval in ticks (visual cadence only).",
                        "Upstream/normal behavior in Isle of Berk 1.2.0: 8 ticks. Optimized default: 8 ticks.",
                        "Upstream/normal behavior is already 8 ticks; retaining 8 preserves its visual cadence.",
                        "Lower values send more packets and make shock visuals denser at higher network/client cost.",
                        "Damage remains fixed at the upstream 20-tick cadence and is not changed by this key."
                )
                .defineInRange("shock_particle_interval_ticks", 8, 1, 200);
        builder.pop();

        SPEC = builder.build();
    }

    private PerformanceConfig() {}

    public static boolean shouldRunAiMove() {
        return !AI_MOVE_THROTTLING_ENABLED.get() || AI_MOVE_INTERVAL_TICKS.get() <= 1;
    }

    public static boolean shouldRunAiMove(int moveTick) {
        return shouldRunAiMove() || moveTick % AI_MOVE_INTERVAL_TICKS.get() == 0;
    }
}
