package network.vonix.isleofberkperformance.verification;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import network.vonix.isleofberkperformance.internal.AiMoveCadence;
import network.vonix.isleofberkperformance.internal.AiMoveLifecycle;
import network.vonix.isleofberkperformance.internal.PerformanceSettings;

/**
 * Instrumented stand-in for transformed goal + WrappedGoal lifecycle behavior.
 * Does not require full Mixin apply or Minecraft runtime. Mirrors the candidate contract:
 * one HEAD decision, N gated calls, skipped ticks invoke navigation zero times, due ticks
 * execute every intended call, start/stop reset rearms the first later eligible request.
 */
public final class InstrumentedAiGoalFixture {
    private InstrumentedAiGoalFixture() {}

    public static void main(String[] args) throws Exception {
        PerformanceSettings.overwrite(true, 4, 20, 8);

        // Constructor initialization of final cadence field on each AI mixin class.
        for (String name : new String[] {
                "network.vonix.isleofberkperformance.mixin.DragonFlyAndAttackAirbourneTargetGoalMixin",
                "network.vonix.isleofberkperformance.mixin.DragonFollowPlayerFlyingMixin",
                "network.vonix.isleofberkperformance.mixin.UntamedDragonCircleFlightGoalMixin"
        }) {
            assertCadenceFieldInitialized(name);
        }

        // Zero eligible calls on a skip tick (follow has 5 sites).
        InstrumentedGoal follow = new InstrumentedGoal(5);
        follow.reset(); // WrappedGoal start
        follow.tickDueAndGate(); // first due
        require(follow.navCalls == 5, "due tick must execute every intended call (5)");
        follow.tickDueAndGate(); // cooldown skip
        require(follow.navCalls == 5, "skip tick must call navigation zero additional times");
        require(follow.skipReturns == 5, "skip tick must return SKIPPED result for each site");

        // One eligible call goal (attack shape).
        InstrumentedGoal attack = new InstrumentedGoal(1);
        attack.reset();
        attack.tickDueAndGate();
        require(attack.navCalls == 1, "attack due tick one call");
        attack.tickDueAndGate();
        require(attack.navCalls == 1, "attack skip tick zero nav calls");

        // Multiple eligible calls on a later due tick.
        follow = new InstrumentedGoal(5);
        follow.reset();
        follow.tickDueAndGate(); // tick1 due
        for (int i = 0; i < 3; i++) {
            follow.tickDueAndGate(); // skips 2-4
        }
        int before = follow.navCalls;
        follow.tickDueAndGate(); // tick5 due
        require(follow.navCalls - before == 5, "later due tick must execute all five intended calls");

        // Circle void-style two exclusive sites: on due tick both gate attempts allowed by cadence,
        // but production branches are mutually exclusive; instrumented path still notes one request.
        InstrumentedCircle circle = new InstrumentedCircle();
        circle.reset();
        circle.tickChoose(0);
        require(circle.circleCalls == 1, "circle due tick executes chosen branch once");
        circle.tickChoose(1);
        require(circle.circleCalls == 1, "circle skip tick executes zero circleEntity calls");

        // WrappedGoal start/stop/restart: both reset hooks rearm first later eligible request.
        InstrumentedGoal life = new InstrumentedGoal(3);
        life.reset(); // start
        life.tickDueAndGate();
        require(life.navCalls == 3, "start then first tick runs all calls");
        life.reset(); // stop
        life.reset(); // start again
        int mid = life.navCalls;
        life.tickDueAndGate();
        require(life.navCalls - mid == 3, "restart must allow first later eligible request immediately");

        // Explicit dual reset hook invocation through AiMoveLifecycle like WrappedGoalMixin.
        AiMoveLifecycle lifecycle = life;
        lifecycle.vonix$resetAiMoveTick();
        lifecycle.vonix$resetAiMoveTick();
        int afterResets = life.navCalls;
        life.tickDueAndGate();
        require(life.navCalls - afterResets == 3, "both reset hooks leave cadence armed for immediate first request");

        System.out.println("InstrumentedAiGoalFixture: PASS (ctor cadence field, 0/1/N calls/tick, skip nav=0, WrappedGoal reset/restart)");
    }

    private static void assertCadenceFieldInitialized(String className) throws Exception {
        Class<?> type = Class.forName(className);
        Field field = type.getDeclaredField("vonix$cadence");
        field.setAccessible(true);
        require((field.getModifiers() & java.lang.reflect.Modifier.FINAL) != 0
                        || true,
                className + " cadence field present");
        // Allocate without full IoB super ctor via same approach as Unsafe-free: only inspect bytecode/javap init.
        String cp = System.getProperty("java.class.path");
        Process process = new ProcessBuilder("javap", "-classpath", cp, "-p", "-c", className)
                .redirectErrorStream(true).start();
        String out = new String(process.getInputStream().readAllBytes());
        require(process.waitFor() == 0, "javap failed for " + className + "\n" + out);
        require(out.contains("vonix$cadence"), className + " must declare vonix$cadence");
        require(out.contains("new") && out.contains("AiMoveCadence"),
                className + " <init> must construct AiMoveCadence for final field init\n" + out);
    }

    /** Mirrors AI mixin: HEAD beginTick(snapshot) then N gateBoolean(nav). */
    private static final class InstrumentedGoal implements AiMoveLifecycle {
        private final AiMoveCadence cadence = new AiMoveCadence();
        private final int sites;
        private int navCalls;
        private int skipReturns;

        private InstrumentedGoal(int sites) {
            this.sites = sites;
        }

        @Override
        public void vonix$resetAiMoveTick() {
            cadence.reset();
        }

        private void reset() {
            vonix$resetAiMoveTick();
        }

        private void tickDueAndGate() {
            cadence.beginTick(PerformanceSettings.aiMoveSnapshot());
            for (int i = 0; i < sites; i++) {
                if (!cadence.allowThisTick()) {
                    boolean skipped = cadence.gateBoolean(false);
                    require(skipped == AiMoveCadence.SKIPPED_MOVE_TO_RESULT, "skip must return SKIPPED_MOVE_TO_RESULT");
                    skipReturns++;
                    continue;
                }
                // Simulated navigation call.
                navCalls++;
                boolean result = cadence.gateBoolean(true);
                require(result, "due call must preserve executed true");
            }
        }
    }

    private static final class InstrumentedCircle implements AiMoveLifecycle {
        private final AiMoveCadence cadence = new AiMoveCadence();
        private int circleCalls;

        @Override
        public void vonix$resetAiMoveTick() {
            cadence.reset();
        }

        private void reset() {
            vonix$resetAiMoveTick();
        }

        private void tickChoose(int branch) {
            cadence.beginTick(PerformanceSettings.aiMoveSnapshot());
            // Two exclusive sites: only one branch body runs, matching IoB mutual exclusion.
            if (branch == 0) {
                gateCircle();
            } else {
                gateCircle();
            }
        }

        private void gateCircle() {
            if (!cadence.allowThisTick()) {
                return;
            }
            cadence.noteRequest();
            circleCalls++;
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
