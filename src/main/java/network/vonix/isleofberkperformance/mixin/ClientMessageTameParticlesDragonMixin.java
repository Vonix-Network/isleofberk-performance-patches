package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.GACMD.isleofberk.network.message.ClientMessageTameParticlesDragon;
import java.util.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Reuses {@code Entity.getRandom()} across the original seven-iteration tame-particle loop.
 * {@code nextGaussian} consumption and particle types remain in the original method.
 */
@Mixin(value = ClientMessageTameParticlesDragon.class, remap = false)
public abstract class ClientMessageTameParticlesDragonMixin {
    @Unique
    private static Random vonix$random;

    @Inject(method = "spawnTamingParticles", at = @At("HEAD"), require = 1, remap = false)
    private static void vonix$beginTameParticles(boolean hearts, ADragonBase dragon, CallbackInfo ci) {
        vonix$random = null;
    }

    @Inject(method = "spawnTamingParticles", at = @At("RETURN"), require = 1, remap = false)
    private static void vonix$endTameParticles(boolean hearts, ADragonBase dragon, CallbackInfo ci) {
        vonix$random = null;
    }

    @Redirect(
            method = "spawnTamingParticles",
            at = @At(value = "INVOKE", target = "Lcom/GACMD/isleofberk/entity/base/dragon/ADragonBase;getRandom()Ljava/util/Random;", remap = true),
            require = 3,
            remap = false
    )
    private static Random vonix$reuseRandom(ADragonBase self) {
        Random cached = vonix$random;
        if (cached == null) {
            cached = self.getRandom();
            vonix$random = cached;
        }
        return cached;
    }
}
