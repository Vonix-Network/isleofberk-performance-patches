package network.vonix.isleofberkperformance.verification;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import network.vonix.isleofberkperformance.internal.AiMoveLifecycle;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Regression fixture for the Mixin class shape that caused the isolated runtime failure.
 * Target mixins must not declare canUse/start/stop; lifecycle reset belongs to WrappedGoal.
 */
public final class MixinLifecycleFixture {
    private static final List<String> TARGET_MIXINS = List.of(
            "network.vonix.isleofberkperformance.mixin.DragonFlyAndAttackAirbourneTargetGoalMixin",
            "network.vonix.isleofberkperformance.mixin.DragonFollowPlayerFlyingMixin",
            "network.vonix.isleofberkperformance.mixin.UntamedDragonCircleFlightGoalMixin"
    );

    private MixinLifecycleFixture() {}

    public static void main(String[] args) throws Exception {
        Class<?> wrappedGoalMixin = Class.forName(
                "network.vonix.isleofberkperformance.mixin.WrappedGoalMixin");
        Method getGoal = wrappedGoalMixin.getDeclaredMethod("getGoal");
        require(Modifier.isPublic(getGoal.getModifiers()),
                "WrappedGoalMixin getGoal shadow must remain public");
        require(Modifier.isAbstract(getGoal.getModifiers()),
                "WrappedGoalMixin getGoal shadow must remain abstract");
        require(getGoal.isAnnotationPresent(Shadow.class),
                "WrappedGoalMixin must shadow the mapped getGoal accessor");
        require(getGoal.getAnnotation(Shadow.class).remap(),
                "WrappedGoalMixin getGoal shadow must use the official-to-SRG mapping boundary");
        try {
            wrappedGoalMixin.getDeclaredField("goal");
            throw new AssertionError("WrappedGoalMixin must not use a fragile private goal field shadow");
        } catch (NoSuchFieldException expected) {
            // The lifecycle hook must resolve the exact public accessor instead.
        }

        for (String name : TARGET_MIXINS) {
            Class<?> mixin = Class.forName(name);
            require(AiMoveLifecycle.class.isAssignableFrom(mixin), name + " must implement AiMoveLifecycle");
            requireNoDeclaredMethod(mixin, "canUse", name + " must preserve target canUse implementation");
            requireNoDeclaredMethod(mixin, "start", name + " must not merge inherited start");
            requireNoDeclaredMethod(mixin, "stop", name + " must not merge inherited stop");
            Method reset = mixin.getDeclaredMethod("vonix$resetAiMoveTick");
            require(Modifier.isPublic(reset.getModifiers()), name + " reset hook must be public for the interface");
        }
        System.out.println("MixinLifecycleFixture: PASS (target canUse/start/stop untouched; WrappedGoal owns reset boundary)");
    }

    private static void requireNoDeclaredMethod(Class<?> type, String method, String message) {
        for (Method declared : type.getDeclaredMethods()) {
            if (declared.getName().equals(method)) {
                throw new AssertionError(message + ": " + declared);
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
