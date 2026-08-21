package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.particles.FireBoltParticle;
import com.GACMD.isleofberk.particles.FireCoatParticle;
import com.GACMD.isleofberk.particles.FlameParticle;
import com.GACMD.isleofberk.particles.FuryBoltParticle;
import com.GACMD.isleofberk.particles.GasParticle;
import com.GACMD.isleofberk.particles.SkrillLightningParticle;
import com.GACMD.isleofberk.particles.SkrillSkillParticle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import network.vonix.isleofberkperformance.internal.ParticleCornerScratch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Reuses per-instance corner vectors and the camera position on IoB particle {@code render}.
 * The original render body is not cancelled or overwritten.
 */
@Mixin(
        value = {
                FireBoltParticle.class,
                FireCoatParticle.class,
                FlameParticle.class,
                FuryBoltParticle.class,
                GasParticle.class,
                SkrillLightningParticle.class,
                SkrillSkillParticle.class
        },
        remap = true
)
public abstract class IoBParticleRenderMixin {
    @Unique
    private final ParticleCornerScratch vonix$corners = new ParticleCornerScratch();

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;F)V", at = @At("HEAD"), require = 1)
    private void vonix$beginParticleRender(VertexConsumer buffer, Camera camera, float partialTicks, CallbackInfo ci) {
        this.vonix$corners.begin();
    }

    @Redirect(
            method = "render(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;F)V",
            at = @At(value = "NEW", target = "(FFF)Lcom/mojang/math/Vector3f;"),
            require = 4
    )
    private Vector3f vonix$reuseCorner(float x, float y, float z) {
        return this.vonix$corners.nextCorner(x, y, z);
    }

    @Redirect(
            method = "render(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/client/Camera;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getPosition()Lnet/minecraft/world/phys/Vec3;"),
            require = 3
    )
    private Vec3 vonix$reuseCameraPosition(Camera camera) {
        return this.vonix$corners.cameraPosition(camera);
    }
}
