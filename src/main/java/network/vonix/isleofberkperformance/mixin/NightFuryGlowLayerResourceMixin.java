package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.dragons.nightfury.NightFury;
import com.GACMD.isleofberk.entity.dragons.nightfury.NightFuryGlowLayer;
import net.minecraft.resources.ResourceLocation;
import network.vonix.isleofberkperformance.internal.RenderResourceCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Reuses Night Fury glow resources without altering glow-variant selection. */
@Mixin(value = NightFuryGlowLayer.class, remap = false)
public abstract class NightFuryGlowLayerResourceMixin {
    @Redirect(method = "getTextureLocation(Lcom/GACMD/isleofberk/entity/dragons/nightfury/NightFury;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "NEW", target = "net/minecraft/resources/ResourceLocation"), remap = false)
    private ResourceLocation vonix$cacheGlowResource(String value) {
        return RenderResourceCache.oneArg(value);
    }
}
