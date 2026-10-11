package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.AI.flight.own.UntamedDragonCircleFlightGoal;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBaseFlyingRideable;
import net.minecraft.world.phys.Vec3;
import network.vonix.isleofberkperformance.internal.AiMoveCadence;
import network.vonix.isleofberkperformance.internal.AiMoveLifecycle;
import network.vonix.isleofberkperformance.internal.PerformanceSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Narrow gate for circle movement; random choice and circle state remain in the original method.
 *
 * <p>One allow/deny decision is computed at {@code tick()} HEAD via {@link AiMoveCadence} from a
 * single atomic {@link PerformanceSettings#aiMoveSnapshot()} read and reused for every gated call
 * in that goal tick. The cadence is reset by {@link WrappedGoalMixin} at the actual wrapped-goal
 * start/stop transition. {@code canUse()} is not a reset point: vanilla
 * {@code Goal.canContinueToUse()} delegates to {@code canUse()} on every running tick and would
 * zero the cadence before each {@code tick()}.
 *
 * <p>Pinned {@code tick()} contains exactly two mutually exclusive
 * {@code ADragonBaseFlyingRideable.circleEntity} invokes. On a due tick the intended branch may run;
 * on a skip tick both sites are suppressed.
 */
@Mixin(value = UntamedDragonCircleFlightGoal.class, remap = true)
public abstract class UntamedDragonCircleFlightGoalMixin implements AiMoveLifecycle {
    @Unique private final AiMoveCadence vonix$cadence = new AiMoveCadence();

    @Override
    public void vonix$resetAiMoveTick() {
        this.vonix$cadence.reset();
    }

    @Inject(method = "tick()V", at = @At("HEAD"), require = 1)
    private void vonix$beginAiMoveTick(CallbackInfo ci) {
        this.vonix$cadence.beginTick(PerformanceSettings.aiMoveSnapshot());
    }

    @Redirect(
            method = "tick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/GACMD/isleofberk/entity/base/dragon/ADragonBaseFlyingRideable;circleEntity(Lnet/minecraft/world/phys/Vec3;FFZIFF)V",
                    remap = false
            ),
            require = 2,
            expect = 2,
            allow = 2
    )
    private void vonix$gateCircleRequest(
            ADragonBaseFlyingRideable dragon,
            Vec3 center,
            float radius,
            float speed,
            boolean clockwise,
            int tick,
            float xScale,
            float zScale
    ) {
        if (!this.vonix$cadence.allowThisTick()) {
            return;
        }
        this.vonix$cadence.noteRequest();
        dragon.circleEntity(center, radius, speed, clockwise, tick, xScale, zScale);
    }
}
