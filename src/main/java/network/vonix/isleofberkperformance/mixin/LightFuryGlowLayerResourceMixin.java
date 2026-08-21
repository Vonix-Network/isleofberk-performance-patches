package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.dragons.lightfury.LightFuryGlowLayer;
import com.GACMD.isleofberk.entity.dragons.nightfury.NightFury;
import net.minecraft.resources.ResourceLocation;
import network.vonix.isleofberkperformance.internal.RenderResourceCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Reuses Light Fury glow resources without altering glow-variant selection. */
@Mixin(value = LightFuryGlowLayer.class, remap = false)
public abstract class LightFuryGlowLayerResourceMixin {
    @Redirect(method = "getTextureLocation(Lcom/GACMD/isleofberk/entity/dragons/nightfury/NightFury;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "NEW", target = "net/minecraft/resources/ResourceLocation"), remap = false)
    private ResourceLocation vonix$cacheGlowResource(String value) {
        return RenderResourceCache.oneArg(value);
    }
}
