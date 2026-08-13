package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.eggs.entity.base.small.ADragonSmallEggBase;
import com.GACMD.isleofberk.entity.eggs.entity.base.small.SmallEggModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reuses fixed egg model resources; dynamic egg texture selection remains untouched. */
@Mixin(value = SmallEggModel.class, remap = false)
public abstract class SmallEggModelMixin {
    private static final ResourceLocation MODEL = new ResourceLocation("isleofberk", "geo/egg/small_egg_model.geo.json");
    private static final ResourceLocation ANIMATION = new ResourceLocation("isleofberk", "animations/dragons/nightfury.animation.json");

    @Inject(method = "getModelLocation(Lcom/GACMD/isleofberk/entity/eggs/entity/base/small/ADragonSmallEggBase;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseModel(ADragonSmallEggBase entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(MODEL); }

    @Inject(method = "getAnimationFileLocation(Lcom/GACMD/isleofberk/entity/eggs/entity/base/small/ADragonSmallEggBase;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseAnimation(ADragonSmallEggBase entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(ANIMATION); }
}
