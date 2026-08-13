package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBolt;
import com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBoltModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reuses fixed projectile model resources; no projectile state is read or changed. */
@Mixin(value = FireBoltModel.class, remap = false)
public abstract class FireBoltModelMixin {
    private static final ResourceLocation MODEL = new ResourceLocation("isleofberk", "geo/projectile/projectile.medium.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation("isleofberk", "textures/projectile/fireball.png");
    private static final ResourceLocation ANIMATION = new ResourceLocation("isleofberk", "animations/projectile/projectile.medium.animation.json");

    @Inject(method = "getModelLocation(Lcom/GACMD/isleofberk/entity/projectile/proj_user/fire_bolt/FireBolt;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseModel(FireBolt entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(MODEL); }

    @Inject(method = "getTextureLocation(Lcom/GACMD/isleofberk/entity/projectile/proj_user/fire_bolt/FireBolt;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseTexture(FireBolt entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(TEXTURE); }

    @Inject(method = "getAnimationFileLocation(Lcom/GACMD/isleofberk/entity/projectile/proj_user/fire_bolt/FireBolt;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseAnimation(FireBolt entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(ANIMATION); }
}
