package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.dragons.montrous_nightmare.MonstrousNightmare;
import com.GACMD.isleofberk.entity.dragons.montrous_nightmare.MonstrousNightmareModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reuses the immutable animation resource that is constant for every entity. */
@Mixin(value = MonstrousNightmareModel.class, remap = false)
public abstract class MonstrousNightmareModelMixin {
    private static final ResourceLocation MODEL = new ResourceLocation("isleofberk", "geo/dragons/nightmare.geo.json");
    private static final ResourceLocation ANIMATION = new ResourceLocation("isleofberk", "animations/dragons/nightmare.animation.json");

    @Inject(method = "getModelLocation(Lcom/GACMD/isleofberk/entity/dragons/montrous_nightmare/MonstrousNightmare;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseModel(MonstrousNightmare entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(MODEL); }

    @Inject(method = "getAnimationFileLocation(Lcom/GACMD/isleofberk/entity/dragons/montrous_nightmare/MonstrousNightmare;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseAnimation(MonstrousNightmare entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(ANIMATION); }
}
