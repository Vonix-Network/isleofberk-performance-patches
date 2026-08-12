package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.dragons.stinger.StingerRender;
import com.GACMD.isleofberk.entity.dragons.stinger.Stinger;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reuses GeckoLib's already-resolved texture parameter instead of resolving it twice. */
@Mixin(value = StingerRender.class, remap = false)
public abstract class StingerRenderMixin {
    @Inject(
            method = "getRenderType(Lcom/GACMD/isleofberk/entity/dragons/stinger/Stinger;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ILnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;",
            at = @At("HEAD"),
            cancellable = true,
            require = 1,
            remap = false
    )
    private void vonix$reuseResolvedTexture(
            Stinger animatable,
            float partialTicks,
            PoseStack stack,
            MultiBufferSource renderTypeBuffer,
            VertexConsumer vertexBuilder,
            int packedLightIn,
            ResourceLocation textureLocation,
            CallbackInfoReturnable<RenderType> cir
    ) {
        cir.setReturnValue(RenderType.entityCutoutNoCull(textureLocation));
    }
}
