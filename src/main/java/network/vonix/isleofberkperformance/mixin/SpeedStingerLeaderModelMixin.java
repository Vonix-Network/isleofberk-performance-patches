package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.dragons.speedstingerleader.SpeedStingerLeader;
import com.GACMD.isleofberk.entity.dragons.speedstingerleader.SpeedStingerLeaderModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Reuses immutable model resources that are constant for every entity. */
@Mixin(value = SpeedStingerLeaderModel.class, remap = false)
public abstract class SpeedStingerLeaderModelMixin {
    private static final ResourceLocation MODEL = new ResourceLocation("isleofberk", "geo/dragons/speed_stinger.geo.json");
    private static final ResourceLocation ANIMATION = new ResourceLocation("isleofberk", "animations/dragons/speed_stinger.animation.json");

    @Inject(method = "getModelLocation(Lcom/GACMD/isleofberk/entity/dragons/speedstingerleader/SpeedStingerLeader;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseModel(SpeedStingerLeader entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(MODEL); }

    @Inject(method = "getAnimationFileLocation(Lcom/GACMD/isleofberk/entity/dragons/speedstingerleader/SpeedStingerLeader;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void vonix$reuseAnimation(SpeedStingerLeader entity, CallbackInfoReturnable<ResourceLocation> cir) { cir.setReturnValue(ANIMATION); }
}
