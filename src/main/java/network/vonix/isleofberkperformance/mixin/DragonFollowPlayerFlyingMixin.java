package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.AI.flight.player.DragonFollowPlayerFlying;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import network.vonix.isleofberkperformance.config.PerformanceConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Configurable gate for exact navigation requests in the follow-player goal. */
@Mixin(value = DragonFollowPlayerFlying.class, remap = true)
public abstract class DragonFollowPlayerFlyingMixin {
    @Unique private int vonix$moveTick;

    @Inject(method = "canUse()Z", at = @At("HEAD"), require = 1)
    private void vonix$resetMoveTickOnCanUse(CallbackInfoReturnable<Boolean> cir) { this.vonix$moveTick = 0; }

    @Inject(method = "stop()V", at = @At("HEAD"), require = 1)
    private void vonix$resetMoveTickOnStop(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) { this.vonix$moveTick = 0; }

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/navigation/PathNavigation;moveTo(DDDD)Z"), expect = 5, require = 1)
    private boolean vonix$gateMoveRequest(PathNavigation navigation, double x, double y, double z, double speed) {
        return PerformanceConfig.shouldRunAiMove(++this.vonix$moveTick) && navigation.moveTo(x, y, z, speed);
    }
}
