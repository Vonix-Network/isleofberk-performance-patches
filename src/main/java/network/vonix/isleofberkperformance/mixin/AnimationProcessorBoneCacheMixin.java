package network.vonix.isleofberkperformance.mixin;

import java.util.List;

import com.GACMD.isleofberk.entity.base.render.model.BaseDragonModel;
import network.vonix.isleofberkperformance.internal.DragonBoneIndex;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.core.processor.IBone;

/**
 * Client-only index for the repeated IoB dragon bone lookups.
 *
 * <p>The original AnimationProcessor list remains authoritative. The index is
 * enabled only for IoB BaseDragonModel instances, invalidated at GeckoLib's
 * list-clear boundary, and validated by list index/identity/name before every
 * cache hit so direct live-list mutations fall back to GeckoLib's lookup.
 */
@Mixin(value = AnimationProcessor.class, remap = false)
public abstract class AnimationProcessorBoneCacheMixin {
    @Shadow @Final private List<IBone> modelRendererList;
    @Shadow @Final private IAnimatableModel animatedModel;

    @Unique
    private DragonBoneIndex vonix$dragonBoneIndex;

    @Inject(
            method = "getBone(Ljava/lang/String;)Lsoftware/bernie/geckolib3/core/processor/IBone;",
            at = @At("HEAD"),
            cancellable = true,
            require = 1,
            remap = false
    )
    private void vonix$lookupIndexedBone(String name, CallbackInfoReturnable<IBone> cir) {
        if (!this.vonix$isIoBDragonModel()) {
            return;
        }
        IBone cached = this.vonix$index().lookup(name, this.modelRendererList);
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }

    @Inject(
            method = "getBone(Ljava/lang/String;)Lsoftware/bernie/geckolib3/core/processor/IBone;",
            at = @At("RETURN"),
            require = 1,
            remap = false
    )
    private void vonix$observeResolvedBone(String name, CallbackInfoReturnable<IBone> cir) {
        if (this.vonix$isIoBDragonModel()) {
            this.vonix$index().observe(name, cir.getReturnValue(), this.modelRendererList);
        }
    }

    @Inject(
            method = "registerModelRenderer(Lsoftware/bernie/geckolib3/core/processor/IBone;)V",
            at = @At("TAIL"),
            require = 1,
            remap = false
    )
    private void vonix$observeRegisteredBone(IBone bone, CallbackInfo ci) {
        if (this.vonix$isIoBDragonModel()) {
            this.vonix$index().observeRegistered(bone, this.modelRendererList);
        }
    }

    @Inject(
            method = "clearModelRendererList()V",
            at = @At("HEAD"),
            require = 1,
            remap = false
    )
    private void vonix$clearBoneIndex(CallbackInfo ci) {
        if (this.vonix$dragonBoneIndex != null) {
            this.vonix$dragonBoneIndex.clear();
        }
    }

    @Unique
    private boolean vonix$isIoBDragonModel() {
        return this.animatedModel instanceof BaseDragonModel;
    }

    @Unique
    private DragonBoneIndex vonix$index() {
        if (this.vonix$dragonBoneIndex == null) {
            this.vonix$dragonBoneIndex = new DragonBoneIndex();
        }
        return this.vonix$dragonBoneIndex;
    }
}
