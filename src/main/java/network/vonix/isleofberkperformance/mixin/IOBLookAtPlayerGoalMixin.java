package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.AI.goal.IOBLookAtPlayerGoal;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import network.vonix.isleofberkperformance.config.CadencePolicy;
import network.vonix.isleofberkperformance.config.PerformanceConfig;

/** Throttles only the optional look-at player scan; upstream look/tick behavior is unchanged. */
@Mixin(value = IOBLookAtPlayerGoal.class, remap = false)
public abstract class IOBLookAtPlayerGoalMixin {
    @Shadow(remap = false) protected ADragonBase dragon;
    @Shadow(remap = false) protected Entity lookAt;

    /**
     * On cadence skip: clear lookAt (no stale target) and cancel canUse as false.
     * Interval 1 never skips (upstream cadence). Phase is dragon UUID.
     */
    @Inject(method = "m_8036_()Z", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$throttlePlayerScan(CallbackInfoReturnable<Boolean> cir) {
        if (!CadencePolicy.shouldRun(dragon.tickCount, PerformanceConfig.lookAtScanInterval.get(), dragon.getUUID())) {
            lookAt = null;
            cir.setReturnValue(false);
        }
    }
}
