package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.dragons.zippleback.ZippleBackModel;
import net.minecraft.resources.ResourceLocation;
import network.vonix.isleofberkperformance.internal.RenderResourceCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Reuses fixed render resources after Isle of Berk selects the variant path. */
@Mixin(value = ZippleBackModel.class, remap = false)
public abstract class ZippleBackModelResourceMixin {
    @Redirect(method = "getModelLocation(Lcom/GACMD/isleofberk/entity/dragons/zippleback/ZippleBack;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "NEW", target = "net/minecraft/resources/ResourceLocation"), remap = false)
    private ResourceLocation vonix$cacheModelResource(String namespace, String path) {
        return RenderResourceCache.twoArg(namespace, path);
    }

    @Redirect(method = "getTextureLocation(Lcom/GACMD/isleofberk/entity/dragons/zippleback/ZippleBack;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "NEW", target = "net/minecraft/resources/ResourceLocation"), remap = false)
    private ResourceLocation vonix$cacheTextureResource(String namespace, String path) {
        return RenderResourceCache.twoArg(namespace, path);
    }

    @Redirect(method = "getAnimationFileLocation(Lcom/GACMD/isleofberk/entity/dragons/zippleback/ZippleBack;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "NEW", target = "net/minecraft/resources/ResourceLocation"), remap = false)
    private ResourceLocation vonix$cacheAnimationResource(String namespace, String path) {
        return RenderResourceCache.twoArg(namespace, path);
    }
}
