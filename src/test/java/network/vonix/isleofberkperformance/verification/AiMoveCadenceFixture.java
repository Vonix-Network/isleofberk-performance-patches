package network.vonix.isleofberkperformance.verification;

import network.vonix.isleofberkperformance.internal.AiMoveCadence;
import network.vonix.isleofberkperformance.internal.PerformanceSettings;

/**
 * Deterministic checks for tick-HEAD AI movement cadence, atomic AI snapshots, and reload rearm.
 * Does not read Forge config values.
 */
public final class AiMoveCadenceFixture {
    private AiMoveCadenceFixture() {}

    public static void main(String[] args) {
        AiMoveCadence cadence = new AiMoveCadence();

        // Armed after start: first eligible request runs immediately at interval 4.
        cadence.reset();
        cadence.beginTick(true, 4);
        require(cadence.allowThisTick(), "armed first tick at interval 4 must allow");
        require(cadence.gateBoolean(false) == false, "allowed moveTo must preserve executed false");
        require(!cadence.armed(), "first gated request must disarm armed state");

        // Cooldown ticks 2-4 skip; every intended call on a skip tick is denied.
        cadence.beginTick(true, 4);
        require(!cadence.allowThisTick(), "tick 2 at interval 4 must skip");
        require(cadence.gateBoolean(false) == AiMoveCadence.SKIPPED_MOVE_TO_RESULT,
                "skipped moveTo must return SKIPPED_MOVE_TO_RESULT");
        cadence.beginTick(true, 4);
        require(!cadence.allowThisTick(), "tick 3 at interval 4 must skip");
        cadence.beginTick(true, 4);
        require(!cadence.allowThisTick(), "tick 4 at interval 4 must skip");

        // Due again on tick 5 (1 + interval); multiple intended calls on the same due tick all run.
        cadence.beginTick(true, 4);
        require(cadence.allowThisTick(), "tick 5 (1 + interval) at interval 4 must allow");
        require(cadence.gateBoolean(true) == true, "first due-tick call must preserve executed true");
        require(cadence.gateBoolean(false) == false, "second due-tick call must still run and preserve false");
        require(cadence.gateBoolean(true) == true, "third due-tick call must still run");

        // Interval 1 keeps every request.
        cadence.reset();
        cadence.beginTick(true, 1);
        require(cadence.allowThisTick(), "interval 1 must allow every tick");
        require(cadence.gateBoolean(true) == true, "interval 1 must preserve executed result");
        cadence.beginTick(true, 1);
        require(cadence.allowThisTick(), "interval 1 must allow the next tick too");

        // Throttling disabled keeps every request regardless of interval.
        cadence.reset();
        cadence.beginTick(false, 4);
        require(cadence.allowThisTick(), "throttling disabled must allow");
        require(cadence.gateBoolean(false) == false, "throttling disabled must preserve executed false");
        cadence.beginTick(false, 4);
        require(cadence.allowThisTick(), "throttling disabled must allow consecutive ticks");

        // Non-positive interval sanitizes to always-on cadence while throttling is enabled.
        cadence.reset();
        cadence.beginTick(true, 0);
        require(cadence.allowThisTick(), "non-positive interval must sanitize to allow");
        require(cadence.intervalSnapshot() == 1, "non-positive interval snapshot must be 1");

        // Void-style gate: noteRequest disarms only once per armed window.
        cadence.reset();
        cadence.beginTick(true, 4);
        require(cadence.allowThisTick(), "void gate armed tick must allow");
        cadence.noteRequest();
        require(!cadence.armed(), "noteRequest must disarm");
        cadence.noteRequest();
        require(!cadence.armed(), "repeated noteRequest on same due tick stays disarmed");
        cadence.beginTick(true, 4);
        require(!cadence.allowThisTick(), "tick after first void request must enter cooldown skip");

        // Start/stop/restart lifecycle: reset re-arms immediately.
        cadence.reset();
        cadence.beginTick(true, 4);
        require(cadence.allowThisTick(), "restart after reset must allow immediately");
        require(cadence.gateBoolean(true) == true, "restart first request must run");

        // Bounded cooldown: long-lived ticks never grow unbounded state.
        cadence.reset();
        cadence.beginTick(true, 4);
        cadence.noteRequest();
        for (int i = 0; i < 64; i++) {
            cadence.beginTick(true, 4);
            int remaining = cadence.cooldownRemaining();
            require(remaining >= 0 && remaining < 4,
                    "cooldown must stay in 0..interval-1 after tick " + i + " remaining=" + remaining);
        }

        // Atomic packed AI snapshot: one long carries a coherent enabled/interval tuple.
        long packed = PerformanceSettings.packAiMove(true, 7);
        require(PerformanceSettings.aiMoveThrottlingEnabled(packed), "packed enabled bit must be true");
        require(PerformanceSettings.aiMoveIntervalTicks(packed) == 7, "packed interval must be 7");
        long packedOff = PerformanceSettings.packAiMove(false, 3);
        require(!PerformanceSettings.aiMoveThrottlingEnabled(packedOff), "packed enabled bit must be false");
        require(PerformanceSettings.aiMoveIntervalTicks(packedOff) == 3, "packed disabled tuple keeps interval 3");

        // beginTick(long) consumes one snapshot without splitting the tuple.
        cadence.reset();
        cadence.beginTick(PerformanceSettings.packAiMove(true, 4));
        require(cadence.allowThisTick(), "packed snapshot beginTick must allow first request");
        require(cadence.gateBoolean(true) == true, "packed snapshot first request must run");

        // SOL-002: interval 20 -> 2 during cooldown rearms immediately under the new interval.
        cadence.reset();
        cadence.beginTick(true, 20);
        require(cadence.allowThisTick(), "interval 20 first tick must allow");
        require(cadence.gateBoolean(true) == true, "interval 20 first request must run");
        cadence.beginTick(true, 20);
        require(!cadence.allowThisTick(), "interval 20 tick 2 must be in cooldown");
        require(cadence.cooldownRemaining() >= 1, "interval 20 cooldown must still be active");
        // Live reload to interval 2 mid-cooldown.
        cadence.beginTick(true, 2);
        require(cadence.allowThisTick(), "20->2 reload must rearm and allow immediately");
        require(cadence.armed(), "20->2 reload must leave cadence armed until first request");
        require(cadence.gateBoolean(true) == true, "20->2 first eligible request must run under new interval");
        require(!cadence.armed(), "20->2 first request must disarm");
        cadence.beginTick(true, 2);
        require(!cadence.allowThisTick(), "after 20->2 request, next tick must skip under interval 2");
        cadence.beginTick(true, 2);
        require(cadence.allowThisTick(), "after 20->2, due again at new interval spacing");

        // SOL-002: interval 2 -> 20 during cooldown rearms immediately under the new interval.
        cadence.reset();
        cadence.beginTick(true, 2);
        require(cadence.allowThisTick(), "interval 2 first tick must allow");
        require(cadence.gateBoolean(true) == true, "interval 2 first request must run");
        cadence.beginTick(true, 2);
        require(!cadence.allowThisTick(), "interval 2 tick 2 must skip");
        // Live reload to interval 20 mid-cooldown (old remaining would have been 0 next).
        cadence.beginTick(true, 20);
        require(cadence.allowThisTick(), "2->20 reload must rearm and allow immediately");
        require(cadence.gateBoolean(true) == true, "2->20 first eligible request must run");
        require(!cadence.armed(), "2->20 first request must disarm");
        // Subsequent spacing uses the new interval 20 (skip 19 ticks).
        for (int i = 0; i < 19; i++) {
            cadence.beginTick(true, 20);
            require(!cadence.allowThisTick(), "2->20 cooldown skip " + (i + 1) + " must deny");
        }
        cadence.beginTick(true, 20);
        require(cadence.allowThisTick(), "2->20 must due again after new interval spacing");

        // Enabled flip also rearms (throttling on -> off -> on).
        cadence.reset();
        cadence.beginTick(true, 4);
        cadence.noteRequest();
        cadence.beginTick(true, 4);
        require(!cadence.allowThisTick(), "precondition: cooldown active before enabled flip");
        cadence.beginTick(false, 4);
        require(cadence.allowThisTick(), "enabled false must allow every tick");
        cadence.beginTick(true, 4);
        require(cadence.allowThisTick(), "re-enable must rearm immediately");
        require(cadence.gateBoolean(true) == true, "re-enable first request must run");

        // PerformanceSettings.overwrite publishes a coherent AI tuple and egg/shock primitives.
        PerformanceSettings.overwrite(false, 9, 11, 13);
        long after = PerformanceSettings.aiMoveSnapshot();
        require(!PerformanceSettings.aiMoveThrottlingEnabled(after), "overwrite must publish enabled=false");
        require(PerformanceSettings.aiMoveIntervalTicks(after) == 9, "overwrite must publish interval=9");
        require(PerformanceSettings.aiMoveThrottlingEnabled() == false, "accessor must match snapshot enabled");
        require(PerformanceSettings.aiMoveIntervalTicks() == 9, "accessor must match snapshot interval");
        require(PerformanceSettings.eggHatchCheckIntervalTicks() == 11, "egg snapshot must update");
        require(PerformanceSettings.shockParticleIntervalTicks() == 13, "shock snapshot must update");
        // Restore defaults used elsewhere.
        PerformanceSettings.overwrite(true, 4, 20, 8);
        long restored = PerformanceSettings.aiMoveSnapshot();
        require(PerformanceSettings.aiMoveThrottlingEnabled(restored), "defaults restored enabled");
        require(PerformanceSettings.aiMoveIntervalTicks(restored) == 4, "defaults restored interval");
        require(PerformanceSettings.eggHatchCheckIntervalTicks() == 20, "defaults restored egg");
        require(PerformanceSettings.shockParticleIntervalTicks() == 8, "defaults restored shock");

        System.out.println("AiMoveCadenceFixture: PASS (tick-HEAD, multi-call, lifecycle, atomic snapshot, 20<->2 reload rearm)");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
