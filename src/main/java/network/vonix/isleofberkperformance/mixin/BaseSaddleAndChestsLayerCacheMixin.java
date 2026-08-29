package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.base.render.layer.BaseSaddleAndChestsLayer;
import com.GACMD.isleofberk.entity.base.render.render.BaseRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Caches the per-render saddle resource without replacing layer rendering. */
@Mixin(value = BaseSaddleAndChestsLayer.class, remap = false)
public abstract class BaseSaddleAndChestsLayerCacheMixin {
    @Shadow
    BaseRenderer<?> baseRenderer;

    @Unique
    private ResourceLocation vonix$cachedSaddleTexture;

    @Inject(method = "getSaddleTexture()Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, remap = false)
    private void vonix$cacheSaddleTexture(CallbackInfoReturnable<ResourceLocation> cir) {
        ResourceLocation cached = this.vonix$cachedSaddleTexture;
        if (cached == null) {
            cached = new ResourceLocation(
                    "isleofberk",
                    "textures/dragons/" + this.baseRenderer.getDragonFolder() + "/equipment.png");
            this.vonix$cachedSaddleTexture = cached;
        }
        cir.setReturnValue(cached);
    }
}
