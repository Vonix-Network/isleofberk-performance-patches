package network.vonix.isleofberkperformance.config;

/**
 * Pure cadence policy kept independent from Minecraft/Forge state for deterministic regression tests.
 * Interval bounds here are the documented safe range shared with {@link PerformanceConfig}.
 */
public final class CadencePolicy {
    /** Inclusive minimum: 1 = upstream every-eligible-pass cadence (no throttle). */
    public static final int INTERVAL_MIN = 1;
    /** Inclusive maximum: hard cap against accidental multi-minute scan freezes. */
    public static final int INTERVAL_MAX = 200;
    /** Default balances fewer scans without extreme lag; override to 1 for baseline. */
    public static final int INTERVAL_DEFAULT = 5;

    private CadencePolicy() {}

    public static boolean shouldRun(int tick, int interval) {
        if (interval <= 1) {
            return true;
        }
        return Math.floorMod(tick, interval) == 0;
    }

    /**
     * Spreads independent entities across the interval instead of making every entity
     * scan on the same tick. The phase is stable for the entity lifetime.
     * A null UUID uses phase offset 0 (same as unphased).
     */
    public static boolean shouldRun(int tick, int interval, java.util.UUID phase) {
        if (interval <= 1) {
            return true;
        }
        int offset = phase == null ? 0 : Math.floorMod(phase.hashCode(), interval);
        return Math.floorMod(tick + offset, interval) == 0;
    }
}
