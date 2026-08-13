package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.dragons.skrill.Skrill;
import com.GACMD.isleofberk.entity.dragons.skrill.SkrillModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reuses immutable model resources that are constant for every entity. */
@Mixin(value = SkrillModel.class, remap = false)
public abstract class SkrillModelMixin {
    private static final ResourceLocation MODEL = new ResourceLocation("isleofberk", "geo/dragons/skrill.geo.json");
    private static final ResourceLocation ANIMATION = new ResourceLocation("isleofberk", "animations/dragons/skrill.animation.json");

    @Inject(method = "getModelLocation(Lcom/GACMD/isleofberk/entity/dragons/skrill/Skrill;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseModel(Skrill entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(MODEL); }

    @Inject(method = "getAnimationFileLocation(Lcom/GACMD/isleofberk/entity/dragons/skrill/Skrill;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseAnimation(Skrill entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(ANIMATION); }
}
