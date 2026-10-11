package network.vonix.isleofberkperformance.internal;

/**
 * Per-goal AI movement cadence. One allow/deny decision is computed at {@code tick()} HEAD
 * and reused for every gated call in that goal tick.
 *
 * <p>After {@link #reset()} (WrappedGoal start/stop), the cadence is armed: every tick stays
 * due until the first gated request, so the first eligible request after start runs. That
 * request starts a tick-based cooldown. Later due ticks allow every intended call; skip ticks
 * suppress all of them.
 *
 * <p>Reload rule: the AI enabled/interval tuple is read once per {@link #beginTick(long)} from
 * an atomic snapshot. If the effective enabled flag or sanitized interval changes versus the
 * previous tick, the cadence deterministically rearms so the next eligible request runs
 * immediately and starts cooldown under the new interval. Old cooldown values are never kept
 * across an effective settings change (for example 20→2 or 2→20 mid-cooldown).
 *
 * <p>Skipped {@code moveTo} calls return {@link #SKIPPED_MOVE_TO_RESULT} ({@code true}) so a
 * suppressed refresh is not reported as a pathfinding failure. When a call is allowed, the
 * original boolean is returned unchanged.
 *
 * <p>State is booleans, a cooldown in {@code 0 .. interval-1}, and the last observed settings
 * pair. No entity/world references, no per-tick allocation, and no unbounded counters.
 */
public final class AiMoveCadence {
    /**
     * Value returned in place of {@code PathNavigation.moveTo} when this tick is not due.
     * {@code true} avoids caller failure/fallback branches; it does not mean a new path was set.
     */
    public static final boolean SKIPPED_MOVE_TO_RESULT = true;

    private boolean armed = true;
    private int cooldownRemaining;
    private boolean allowThisTick;
    private boolean throttlingActive;
    private int interval = 1;
    private boolean hasSettings;
    private boolean lastThrottlingEnabled = true;
    private int lastInterval = 1;

    public void reset() {
        this.armed = true;
        this.cooldownRemaining = 0;
        this.allowThisTick = false;
        this.throttlingActive = false;
        this.interval = 1;
        // Keep last settings so a reload while stopped is still observed on the next beginTick.
    }

    /**
     * Compute the single allow/deny decision for this goal tick from one atomic AI snapshot.
     * Does not allocate.
     *
     * @param aiMoveSnapshot packed tuple from {@link PerformanceSettings#aiMoveSnapshot()}
     */
    public void beginTick(long aiMoveSnapshot) {
        beginTick(
                PerformanceSettings.aiMoveThrottlingEnabled(aiMoveSnapshot),
                PerformanceSettings.aiMoveIntervalTicks(aiMoveSnapshot)
        );
    }

    /**
     * Compute the single allow/deny decision for this goal tick.
     *
     * @param throttlingEnabled live snapshot of {@code ai_move_throttling_enabled}
     * @param intervalTicks live snapshot of {@code ai_move_interval_ticks}
     */
    public void beginTick(boolean throttlingEnabled, int intervalTicks) {
        int sanitizedInterval = intervalTicks < 1 ? 1 : intervalTicks;
        if (this.hasSettings
                && (this.lastThrottlingEnabled != throttlingEnabled
                || this.lastInterval != sanitizedInterval)) {
            // Effective AI settings changed: drop old cooldown and rearm under the new tuple.
            this.armed = true;
            this.cooldownRemaining = 0;
        }
        this.hasSettings = true;
        this.lastThrottlingEnabled = throttlingEnabled;
        this.lastInterval = sanitizedInterval;
        this.interval = sanitizedInterval;
        this.throttlingActive = throttlingEnabled && sanitizedInterval > 1;
        if (!this.throttlingActive) {
            this.allowThisTick = true;
            this.armed = true;
            this.cooldownRemaining = 0;
            return;
        }
        if (this.armed) {
            this.allowThisTick = true;
            return;
        }
        if (this.cooldownRemaining <= 0) {
            this.allowThisTick = true;
            this.cooldownRemaining = sanitizedInterval - 1;
            return;
        }
        this.allowThisTick = false;
        this.cooldownRemaining--;
    }

    public boolean allowThisTick() {
        return this.allowThisTick;
    }

    /**
     * Marks that a gated request was issued on a due tick. The first such call after start
     * or reload-rearm disarms the cadence and starts the tick cooldown. Later calls on the
     * same due tick are no-ops so every intended site may still run.
     */
    public void noteRequest() {
        if (!this.throttlingActive || !this.armed) {
            return;
        }
        this.armed = false;
        this.cooldownRemaining = this.interval - 1;
    }

    public boolean gateBoolean(boolean executedResult) {
        if (!this.allowThisTick) {
            return SKIPPED_MOVE_TO_RESULT;
        }
        noteRequest();
        return executedResult;
    }

    public boolean armed() {
        return this.armed;
    }

    public int cooldownRemaining() {
        return this.cooldownRemaining;
    }

    public int intervalSnapshot() {
        return this.interval;
    }

    public boolean lastThrottlingEnabled() {
        return this.lastThrottlingEnabled;
    }
}
