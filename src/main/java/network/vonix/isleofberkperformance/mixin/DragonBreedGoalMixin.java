package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.AI.breed.DragonBreedGoal;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import network.vonix.isleofberkperformance.config.CadencePolicy;
import network.vonix.isleofberkperformance.config.PerformanceConfig;

/** Throttles only the partner search; breeding continuation and execution are untouched. */
@Mixin(value = DragonBreedGoal.class, remap = false)
public abstract class DragonBreedGoalMixin {
    @Shadow(remap = false) protected ADragonBase animal;
    @Shadow(remap = false) protected ADragonBase partner;

    /**
     * On cadence skip: clear partner (no stale partner) and cancel canUse as false.
     * Interval 1 never skips (upstream cadence). Phase is animal UUID.
     */
    @Inject(method = "m_8036_()Z", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$throttlePartnerScan(CallbackInfoReturnable<Boolean> cir) {
        if (!CadencePolicy.shouldRun(animal.tickCount, PerformanceConfig.breedScanInterval.get(), animal.getUUID())) {
            partner = null;
            cir.setReturnValue(false);
        }
    }
}
