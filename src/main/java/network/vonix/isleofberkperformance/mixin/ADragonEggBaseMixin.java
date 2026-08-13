package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import network.vonix.isleofberkperformance.config.PerformanceConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/** Changes only the upstream hatch-check cadence constant; hatch side effects and state updates remain original. */
@Mixin(value = ADragonEggBase.class, remap = true)
public abstract class ADragonEggBaseMixin {
    @ModifyConstant(method = "tick()V", constant = @Constant(intValue = 20), require = 1)
    private int vonix$configureHatchCheckInterval(int upstreamInterval) {
        return PerformanceConfig.EGG_HATCH_CHECK_INTERVAL_TICKS.get();
    }
}
