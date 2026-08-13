package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.AI.flight.own.UntamedDragonCircleFlightGoal;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBaseFlyingRideable;
import net.minecraft.world.phys.Vec3;
import network.vonix.isleofberkperformance.config.PerformanceConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Narrow HEAD gate for circle movement; random choice and circle state remain in the original method. */
@Mixin(value = UntamedDragonCircleFlightGoal.class, remap = true)
public abstract class UntamedDragonCircleFlightGoalMixin {
    @Unique private int vonix$moveTick;

    @Inject(method = "canUse()Z", at = @At("HEAD"), require = 1)
    private void vonix$resetMoveTickOnCanUse(CallbackInfoReturnable<Boolean> cir) { this.vonix$moveTick = 0; }

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/base/dragon/ADragonBaseFlyingRideable;circleEntity(Lnet/minecraft/world/phys/Vec3;FFZIFF)V", remap = false), expect = 2, require = 1)
    private void vonix$gateCircleRequest(ADragonBaseFlyingRideable dragon, Vec3 center, float radius, float speed, boolean clockwise, int tick, float xScale, float zScale) {
        if (PerformanceConfig.shouldRunAiMove(++this.vonix$moveTick)) {
            dragon.circleEntity(center, radius, speed, clockwise, tick, xScale, zScale);
        }
    }
}
