package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.dragons.terrible_terror.TerribleTerror;
import com.GACMD.isleofberk.entity.dragons.terrible_terror.TerribleTerrorModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reuses the immutable animation resource that is constant for every entity. */
@Mixin(value = TerribleTerrorModel.class, remap = false)
public abstract class TerribleTerrorModelMixin {
    private static final ResourceLocation ANIMATION = new ResourceLocation("isleofberk", "animations/dragons/terrible_terror.animation.json");

    @Inject(method = "getAnimationFileLocation(Lcom/GACMD/isleofberk/entity/dragons/terrible_terror/TerribleTerror;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseAnimation(TerribleTerror entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(ANIMATION); }
}
