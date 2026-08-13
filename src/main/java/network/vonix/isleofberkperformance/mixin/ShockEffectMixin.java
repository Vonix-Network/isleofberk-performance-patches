package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.effects.ShockEffect;
import network.vonix.isleofberkperformance.config.PerformanceConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Configures particle cadence only; the separate upstream damage constant remains 20 ticks.
 * Pinned Isle of Berk 1.2.0 {@code ShockEffect.applyEffectTick} has {@code bipush 20} then {@code bipush 8}.
 * This injector matches only {@code intValue = 8} ({@code ordinal = 0} among 8s), so the damage 20 is untouched.
 */
@Mixin(value = ShockEffect.class, remap = true)
public abstract class ShockEffectMixin {
    @ModifyConstant(
            method = "applyEffectTick(Lnet/minecraft/world/entity/LivingEntity;I)V",
            constant = @Constant(intValue = 8, ordinal = 0),
            require = 1
    )
    private int vonix$configureParticleInterval(int upstreamInterval) {
        return PerformanceConfig.SHOCK_PARTICLE_INTERVAL_TICKS.get();
    }
}
