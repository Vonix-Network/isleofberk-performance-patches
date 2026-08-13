package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.AI.flight.own.UntamedDragonCircleFlightGoal;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBaseFlyingRideable;
import net.minecraft.world.phys.Vec3;
import network.vonix.isleofberkperformance.config.PerformanceConfig;
import network.vonix.isleofberkperformance.internal.AiMoveLifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Narrow gate for circle movement; random choice and circle state remain in the original method.
 *
 * <p>The counter is reset by {@link WrappedGoalMixin} at the actual wrapped-goal start/stop
 * transition. {@code canUse()} is not a reset point: vanilla {@code Goal.canContinueToUse()}
 * delegates to {@code canUse()} on every running tick and would zero the counter before each
 * {@code tick()}.
 *
 * <p>Pinned {@code tick()} contains exactly two mutually exclusive
 * {@code ADragonBaseFlyingRideable.circleEntity} invokes.
 */
@Mixin(value = UntamedDragonCircleFlightGoal.class, remap = true)
public abstract class UntamedDragonCircleFlightGoalMixin implements AiMoveLifecycle {
    @Unique private int vonix$moveTick;

    @Override
    public void vonix$resetAiMoveTick() {
        this.vonix$moveTick = 0;
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
        if (PerformanceConfig.shouldRunAiMove(++this.vonix$moveTick)) {
            dragon.circleEntity(center, radius, speed, clockwise, tick, xScale, zScale);
        }
    }
}
