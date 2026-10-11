package network.vonix.isleofberkperformance.internal;

/**
 * Reload-correct primitive snapshots of the COMMON spec. Mixins read only these values.
 * Defaults match {@code PerformanceConfig} definitions and are replaced on Forge config
 * load/reload — never frozen at class initialization from {@code ForgeConfigSpec.get()}.
 *
 * <p>The AI enabled flag and interval are published as one packed {@code long} snapshot so a
 * reader observes a coherent tuple. Hot paths must call {@link #aiMoveSnapshot()} once per
 * decision and unpack with {@link #aiMoveThrottlingEnabled(long)} / {@link #aiMoveIntervalTicks(long)}.
 * Egg and shock intervals remain individual primitive snapshots.
 */
public final class PerformanceSettings {
    private static final int AI_INTERVAL_MIN = 1;
    private static final int AI_INTERVAL_MAX = 20;
    private static final int CADENCE_MIN = 1;
    private static final int CADENCE_MAX = 200;

    /** Packed AI tuple: interval in the high 32 bits, enabled flag in bit 0. */
    private static volatile long aiMoveSnapshot = packAiMove(true, 4);
    private static volatile int eggHatchCheckIntervalTicks = 20;
    private static volatile int shockParticleIntervalTicks = 8;

    private PerformanceSettings() {}

    public static void overwrite(
            boolean throttlingEnabled,
            int aiIntervalTicks,
            int eggIntervalTicks,
            int shockIntervalTicks
    ) {
        int clampedAi = clamp(aiIntervalTicks, AI_INTERVAL_MIN, AI_INTERVAL_MAX);
        // Single volatile write publishes the coherent AI enabled/interval tuple.
        aiMoveSnapshot = packAiMove(throttlingEnabled, clampedAi);
        eggHatchCheckIntervalTicks = clamp(eggIntervalTicks, CADENCE_MIN, CADENCE_MAX);
        shockParticleIntervalTicks = clamp(shockIntervalTicks, CADENCE_MIN, CADENCE_MAX);
    }

    /**
     * Atomic AI enabled/interval snapshot. Read once per goal-tick decision; do not allocate.
     */
    public static long aiMoveSnapshot() {
        return aiMoveSnapshot;
    }

    public static long packAiMove(boolean throttlingEnabled, int intervalTicks) {
        int clamped = clamp(intervalTicks, AI_INTERVAL_MIN, AI_INTERVAL_MAX);
        return (((long) clamped) << 32) | (throttlingEnabled ? 1L : 0L);
    }

    public static boolean aiMoveThrottlingEnabled(long snapshot) {
        return (snapshot & 1L) != 0L;
    }

    public static int aiMoveIntervalTicks(long snapshot) {
        return (int) (snapshot >>> 32);
    }

    public static boolean aiMoveThrottlingEnabled() {
        return aiMoveThrottlingEnabled(aiMoveSnapshot);
    }

    public static int aiMoveIntervalTicks() {
        return aiMoveIntervalTicks(aiMoveSnapshot);
    }

    public static int eggHatchCheckIntervalTicks() {
        return eggHatchCheckIntervalTicks;
    }

    public static int shockParticleIntervalTicks() {
        return shockParticleIntervalTicks;
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
