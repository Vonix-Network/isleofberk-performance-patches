package network.vonix.isleofberkperformance.mixin;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import network.vonix.isleofberkperformance.internal.AiMoveLifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Resets patched goals at the actual GoalSelector lifecycle boundary.
 *
 * <p>The pinned 1.18.2 {@code WrappedGoal.start()} and {@code stop()} methods are the
 * transition points used by {@code GoalSelector}; each delegates to its wrapped goal.
 * Injecting here avoids merging inherited lifecycle methods into the IoB target classes.
 */
@Mixin(value = WrappedGoal.class, remap = true)
public abstract class WrappedGoalMixin {
    /**
     * The official mapping boundary for this accessor is getGoal() -> m_26015_.
     * Keeping this shadow remapped lets the compiled dev mixin use getGoal() while
     * the reobfuscated production mixin resolves m_26015_.
     */
    @Shadow
    public abstract Goal getGoal();

    @Inject(method = "start()V", at = @At("HEAD"), require = 1)
    private void vonix$resetPatchedGoalOnStart(CallbackInfo ci) {
        if (this.getGoal() instanceof AiMoveLifecycle lifecycle) {
            lifecycle.vonix$resetAiMoveTick();
        }
    }

    @Inject(method = "stop()V", at = @At("HEAD"), require = 1)
    private void vonix$resetPatchedGoalOnStop(CallbackInfo ci) {
        if (this.getGoal() instanceof AiMoveLifecycle lifecycle) {
            lifecycle.vonix$resetAiMoveTick();
        }
    }
}
