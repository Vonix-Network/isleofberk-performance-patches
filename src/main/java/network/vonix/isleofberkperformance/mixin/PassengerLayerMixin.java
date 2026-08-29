package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.base.dragon.ADragonBase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import network.vonix.isleofberkperformance.PassengerRenderGate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

/**
 * Optional client-only inject into Variant Loader 2.7.0 PassengerLayer.renderPassenger.
 * Cancels only when the exact dragon passenger list is empty. Non-empty lists use the
 * original Variant Loader method path unchanged.
 */
@Pseudo
@Mixin(targets = "nordmods.iobvariantloader.util.layer.PassengerLayer", remap = false)
public abstract class PassengerLayerMixin {
    private static final String TARGET_METHOD = "renderPassenger";
    private static final String TARGET_DESCRIPTOR =
            "(Lcom/GACMD/isleofberk/entity/base/dragon/ADragonBase;Lsoftware/bernie/geckolib3/model/AnimatedGeoModel;Lsoftware/bernie/geckolib3/geo/render/built/GeoBone;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lsoftware/bernie/geckolib3/renderers/geo/GeoEntityRenderer;I)V";
    private static final String TARGET_SELECTOR = TARGET_METHOD + TARGET_DESCRIPTOR;

    @Inject(
            method = TARGET_SELECTOR,
            at = @At("HEAD"),
            cancellable = true,
            require = 1,
            remap = false
    )
    @SuppressWarnings("rawtypes")
    private static void isleofberkperformance$cancelEmptyPassengers(
            ADragonBase animatable,
            AnimatedGeoModel modelProvider,
            GeoBone bone,
            PoseStack stack,
            VertexConsumer bufferIn,
            GeoEntityRenderer renderer,
            int packedLightIn,
            CallbackInfo ci
    ) {
        if (PassengerRenderGate.shouldCancelEmptyPassengerRender(animatable.getPassengers())) {
            ci.cancel();
        }
    }
}
