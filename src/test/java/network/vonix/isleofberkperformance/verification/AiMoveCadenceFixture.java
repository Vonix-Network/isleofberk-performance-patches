package network.vonix.isleofberkperformance.verification;

import network.vonix.isleofberkperformance.config.PerformanceConfig;

/**
 * Deterministic predicate check for the documented first-request-then-interval AI cadence.
 * Does not read Forge config values.
 */
public final class AiMoveCadenceFixture {
    private AiMoveCadenceFixture() {}

    public static void main(String[] args) {
        require(PerformanceConfig.shouldRunThrottledAiMove(1, 4), "first request at interval 4 must run");
        require(!PerformanceConfig.shouldRunThrottledAiMove(2, 4), "tick 2 at interval 4 must skip");
        require(!PerformanceConfig.shouldRunThrottledAiMove(3, 4), "tick 3 at interval 4 must skip");
        require(PerformanceConfig.shouldRunThrottledAiMove(5, 4), "tick 5 (1 + interval) at interval 4 must run");
        require(PerformanceConfig.shouldRunThrottledAiMove(1, 1), "interval 1 must run every tick");
        require(PerformanceConfig.shouldRunThrottledAiMove(2, 1), "interval 1 must run every tick");
        require(!PerformanceConfig.shouldRunThrottledAiMove(0, 4), "zero tick is not a request site");
        require(!PerformanceConfig.shouldRunThrottledAiMove(1, 0), "non-positive interval is not a cadence");
        System.out.println("AiMoveCadenceFixture: PASS (first request immediately, then every interval)");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
