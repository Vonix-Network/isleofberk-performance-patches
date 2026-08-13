package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.AI.flight.player.DragonFollowPlayerFlying;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import network.vonix.isleofberkperformance.config.PerformanceConfig;
import network.vonix.isleofberkperformance.internal.AiMoveLifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Configurable gate for exact navigation requests in the follow-player goal.
 *
 * <p>The counter is reset by {@link WrappedGoalMixin} at the actual wrapped-goal start/stop
 * transition. {@code canUse()} is not a reset point: vanilla {@code Goal.canContinueToUse()}
 * delegates to {@code canUse()} on every running tick and would zero the counter before each
 * {@code tick()}.
 *
 * <p>Pinned {@code tick()} contains exactly five {@code PathNavigation.moveTo(double, double, double, double)}.
 */
@Mixin(value = DragonFollowPlayerFlying.class, remap = true)
public abstract class DragonFollowPlayerFlyingMixin implements AiMoveLifecycle {
    @Unique private int vonix$moveTick;

    @Override
    public void vonix$resetAiMoveTick() {
        this.vonix$moveTick = 0;
    }

    @Redirect(
            method = "tick()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;moveTo(DDDD)Z"),
            require = 5,
            expect = 5,
            allow = 5
    )
    private boolean vonix$gateMoveRequest(PathNavigation navigation, double x, double y, double z, double speed) {
        return PerformanceConfig.shouldRunAiMove(++this.vonix$moveTick) && navigation.moveTo(x, y, z, speed);
    }
}
