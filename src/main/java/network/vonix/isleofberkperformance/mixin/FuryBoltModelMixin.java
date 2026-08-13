package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.projectile.proj_user.furybolt.FuryBolt;
import com.GACMD.isleofberk.entity.projectile.proj_user.furybolt.FuryBoltModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reuses fixed projectile model resources; variant texture selection remains untouched. */
@Mixin(value = FuryBoltModel.class, remap = false)
public abstract class FuryBoltModelMixin {
    private static final ResourceLocation MODEL = new ResourceLocation("isleofberk", "geo/projectile/fury.bolt.geo.json");
    private static final ResourceLocation ANIMATION = new ResourceLocation("isleofberk", "animations/projectile/fury_bolt.animation.json");

    @Inject(method = "getModelLocation(Lcom/GACMD/isleofberk/entity/projectile/proj_user/furybolt/FuryBolt;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseModel(FuryBolt entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(MODEL); }

    @Inject(method = "getAnimationFileLocation(Lcom/GACMD/isleofberk/entity/projectile/proj_user/furybolt/FuryBolt;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseAnimation(FuryBolt entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(ANIMATION); }
}
