package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.AI.flight.own.DragonFlyAndAttackAirbourneTargetGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import network.vonix.isleofberkperformance.config.PerformanceConfig;
import network.vonix.isleofberkperformance.internal.AiMoveLifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Gates only the exact target-flight navigation request; target selection and flight state remain upstream.
 *
 * <p>The counter is reset by {@link WrappedGoalMixin} at the actual wrapped-goal start/stop
 * transition. {@code canUse()} is not a reset point: vanilla {@code Goal.canContinueToUse()}
 * delegates to {@code canUse()} on every running tick and would zero the counter before each
 * {@code tick()}.
 *
 * <p>Pinned {@code tick()} contains exactly one {@code PathNavigation.moveTo(Entity, double)}.
 */
@Mixin(value = DragonFlyAndAttackAirbourneTargetGoal.class, remap = true)
public abstract class DragonFlyAndAttackAirbourneTargetGoalMixin implements AiMoveLifecycle {
    @Unique private int vonix$moveTick;

    @Override
    public void vonix$resetAiMoveTick() {
        this.vonix$moveTick = 0;
    }

    @Redirect(
            method = "tick()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;moveTo(Lnet/minecraft/world/entity/Entity;D)Z"),
            require = 1,
            expect = 1,
            allow = 1
    )
    private boolean vonix$gateMoveRequest(PathNavigation navigation, Entity target, double speed) {
        return PerformanceConfig.shouldRunAiMove(++this.vonix$moveTick) && navigation.moveTo(target, speed);
    }
}
