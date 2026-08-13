package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.eggs.entity.base.large.ADragonLargeEggBase;
import com.GACMD.isleofberk.entity.eggs.entity.base.large.ADragonLargeEggModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reuses fixed egg model resources; dynamic egg texture selection remains untouched. */
@Mixin(value = ADragonLargeEggModel.class, remap = false)
public abstract class LargeEggModelMixin {
    private static final ResourceLocation MODEL = new ResourceLocation("isleofberk", "geo/egg/large_egg_model.geo.json");
    private static final ResourceLocation ANIMATION = new ResourceLocation("isleofberk", "animations/egg/nightfury.animation.json");

    @Inject(method = "getModelLocation(Lcom/GACMD/isleofberk/entity/eggs/entity/base/large/ADragonLargeEggBase;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseModel(ADragonLargeEggBase entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(MODEL); }

    @Inject(method = "getAnimationFileLocation(Lcom/GACMD/isleofberk/entity/eggs/entity/base/large/ADragonLargeEggBase;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseAnimation(ADragonLargeEggBase entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(ANIMATION); }
}
