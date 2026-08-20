package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.AI.flight.player.DragonFollowPlayerFlying;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import network.vonix.isleofberkperformance.internal.AiMoveCadence;
import network.vonix.isleofberkperformance.internal.AiMoveLifecycle;
import network.vonix.isleofberkperformance.internal.PerformanceSettings;
import network.vonix.isleofberkperformance.internal.TailingDragonLifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Configurable gate for exact navigation requests in the follow-player goal.
 *
 * <p>One allow/deny decision is computed at {@code tick()} HEAD via {@link AiMoveCadence} from a
 * single atomic {@link PerformanceSettings#aiMoveSnapshot()} read and reused for every gated call
 * in that goal tick. The cadence is reset by {@link WrappedGoalMixin} at the actual wrapped-goal
 * start/stop transition. {@code canUse()} is not a reset point: vanilla
 * {@code Goal.canContinueToUse()} delegates to {@code canUse()} on every running tick and would
 * zero the cadence before each {@code tick()}.
 *
 * <p>Pinned {@code tick()} contains exactly five {@code PathNavigation.moveTo(double, double, double, double)}.
 * On a due tick every intended site may run; on a skip tick all return
 * {@link AiMoveCadence#SKIPPED_MOVE_TO_RESULT}.
 */
@Mixin(value = DragonFollowPlayerFlying.class, remap = true)
public abstract class DragonFollowPlayerFlyingMixin implements AiMoveLifecycle {
    @Unique private final AiMoveCadence vonix$cadence = new AiMoveCadence();

    @Override
    public void vonix$resetAiMoveTick() {
        this.vonix$cadence.reset();
    }

    @Inject(method = "tick()V", at = @At("HEAD"), require = 1)
    private void vonix$beginAiMoveTick(CallbackInfo ci) {
        this.vonix$cadence.beginTick(PerformanceSettings.aiMoveSnapshot());
    }

    /**
     * The upstream map participates in active formation offsets through {@code size()}, so it is
     * retained unchanged during ticks. Once this exact goal has stopped, no active formation can
     * observe it and the goal can release the owner/dragon entry.
     */
    @Inject(method = "stop()V", at = @At("TAIL"), require = 1)
    private void vonix$clearTailingDragonsOnStop(CallbackInfo ci) {
        ((TailingDragonLifecycle) this).vonix$clearTailingDragons();
    }

    @Redirect(
            method = "tick()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;moveTo(DDDD)Z"),
            require = 5,
            expect = 5,
            allow = 5
    )
    private boolean vonix$gateMoveRequest(PathNavigation navigation, double x, double y, double z, double speed) {
        if (!this.vonix$cadence.allowThisTick()) {
            return AiMoveCadence.SKIPPED_MOVE_TO_RESULT;
        }
        return this.vonix$cadence.gateBoolean(navigation.moveTo(x, y, z, speed));
    }
}
