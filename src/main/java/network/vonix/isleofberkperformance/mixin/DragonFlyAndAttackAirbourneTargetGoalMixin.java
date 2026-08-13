package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.AI.flight.own.DragonFlyAndAttackAirbourneTargetGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import network.vonix.isleofberkperformance.config.PerformanceConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Gates only the exact target-flight navigation request; target selection and flight state remain upstream. */
@Mixin(value = DragonFlyAndAttackAirbourneTargetGoal.class, remap = true)
public abstract class DragonFlyAndAttackAirbourneTargetGoalMixin {
    @Unique private int vonix$moveTick;

    @Inject(method = "canUse()Z", at = @At("HEAD"), require = 1)
    private void vonix$resetMoveTickOnCanUse(CallbackInfoReturnable<Boolean> cir) { this.vonix$moveTick = 0; }

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;moveTo(Lnet/minecraft/world/entity/Entity;D)Z"), require = 1)
    private boolean vonix$gateMoveRequest(PathNavigation navigation, Entity target, double speed) {
        return PerformanceConfig.shouldRunAiMove(++this.vonix$moveTick) && navigation.moveTo(target, speed);
    }
}
