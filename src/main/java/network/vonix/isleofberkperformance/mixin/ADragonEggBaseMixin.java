package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase;
import network.vonix.isleofberkperformance.internal.PerformanceSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Changes only the upstream hatch-check cadence constant; hatch side effects and state updates remain original.
 * Pinned Isle of Berk 1.2.0 {@code ADragonEggBase.tick()} contains exactly one {@code bipush 20}
 * ({@code tickCount % 20 == 0}). {@code ordinal = 0} pins that sole match.
 *
 * <p>Reads the reload-correct primitive from {@link PerformanceSettings}; ForgeConfigSpec.get()
 * stays confined to the config load/reload path.
 */
@Mixin(value = ADragonEggBase.class, remap = true)
public abstract class ADragonEggBaseMixin {
    @ModifyConstant(method = "tick()V", constant = @Constant(intValue = 20, ordinal = 0), require = 1)
    private int vonix$configureHatchCheckInterval(int upstreamInterval) {
        return PerformanceSettings.eggHatchCheckIntervalTicks();
    }
}
