package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.AI.flight.own.DragonFlyAndAttackAirbourneTargetGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
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
 * Gates only the exact target-flight navigation request; target selection and flight state remain upstream.
 *
 * <p>One allow/deny decision is computed at {@code tick()} HEAD via {@link AiMoveCadence} from a
 * single atomic {@link PerformanceSettings#aiMoveSnapshot()} read and reused for every gated call
 * in that goal tick. The cadence is reset by {@link WrappedGoalMixin} at the actual wrapped-goal
 * start/stop transition. {@code canUse()} is not a reset point: vanilla
 * {@code Goal.canContinueToUse()} delegates to {@code canUse()} on every running tick and would
 * zero the cadence before each {@code tick()}.
 *
 * <p>Pinned {@code tick()} contains exactly one {@code PathNavigation.moveTo(Entity, double)}.
 * Skipped calls return {@link AiMoveCadence#SKIPPED_MOVE_TO_RESULT} so a suppressed refresh is not
 * reported as a pathfinding failure.
 */
@Mixin(value = DragonFlyAndAttackAirbourneTargetGoal.class, remap = true)
public abstract class DragonFlyAndAttackAirbourneTargetGoalMixin implements AiMoveLifecycle {
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;moveTo(Lnet/minecraft/world/entity/Entity;D)Z"),
            require = 1,
            expect = 1,
            allow = 1
    )
    private boolean vonix$gateMoveRequest(PathNavigation navigation, Entity target, double speed) {
        if (!this.vonix$cadence.allowThisTick()) {
            return AiMoveCadence.SKIPPED_MOVE_TO_RESULT;
        }
        return this.vonix$cadence.gateBoolean(navigation.moveTo(target, speed));
    }
}
