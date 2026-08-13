package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.dragons.lightfury.LightFury;
import com.GACMD.isleofberk.entity.dragons.lightfury.LightFuryModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reuses immutable model resources that are constant for every entity. */
@Mixin(value = LightFuryModel.class, remap = false)
public abstract class LightFuryModelMixin {
    private static final ResourceLocation MODEL = new ResourceLocation("isleofberk", "geo/dragons/light_fury.geo.json");
    private static final ResourceLocation ANIMATION = new ResourceLocation("isleofberk", "animations/dragons/light_fury.animation.json");

    @Inject(method = "getModelLocation(Lcom/GACMD/isleofberk/entity/dragons/lightfury/LightFury;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseModel(LightFury entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(MODEL); }

    @Inject(method = "getAnimationFileLocation(Lcom/GACMD/isleofberk/entity/dragons/lightfury/LightFury;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseAnimation(LightFury entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(ANIMATION); }
}
