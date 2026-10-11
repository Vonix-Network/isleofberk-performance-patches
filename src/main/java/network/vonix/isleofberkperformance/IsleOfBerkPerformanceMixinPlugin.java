package network.vonix.isleofberkperformance;

import java.util.List;
import java.util.Set;

import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Skips the 13 dragon model-resource Mixins when {@code iobvariantloader} is installed.
 * Variant Loader replaces the same resource methods, so the {@code NEW ResourceLocation}
 * Redirects have no injection point. Other Mixins stay applied.
 */
public final class IsleOfBerkPerformanceMixinPlugin implements IMixinConfigPlugin {
    public static final String VARIANT_LOADER_MOD_ID = "iobvariantloader";

    /** Simple mixin names that overlap Variant Loader resource selection. */
    public static final Set<String> VARIANT_LOADER_OVERLAP_MIXINS = Set.of(
            "GronckleModelResourceMixin",
            "TripleStrykeModelResourceMixin",
            "TerribleTerrorModelResourceMixin",
            "StingerModelResourceMixin",
            "LightFuryModelResourceMixin",
            "DeadlyNadderModelResourceMixin",
            "MonstrousNightmareModelResourceMixin",
            "NightFuryModelResourceMixin",
            "NightLightModelResourceMixin",
            "SkrillModelResourceMixin",
            "SpeedStingerModelResourceMixin",
            "SpeedStingerLeaderModelResourceMixin",
            "ZippleBackModelResourceMixin"
    );

    /**
     * Testable skip helper: decline only the overlap set, and only when Variant Loader is present.
     */
    public static boolean shouldApplyMixin(String mixinClassName, boolean variantLoaderPresent) {
        if (!variantLoaderPresent) {
            return true;
        }
        return !VARIANT_LOADER_OVERLAP_MIXINS.contains(simpleMixinName(mixinClassName));
    }

    public static String simpleMixinName(String mixinClassName) {
        int dot = mixinClassName.lastIndexOf('.');
        return dot < 0 ? mixinClassName : mixinClassName.substring(dot + 1);
    }

    /**
     * True only when FML already lists {@code iobvariantloader}. A null, uninitialized,
     * or failed {@link LoadingModList} lookup fails closed (treated as absent).
     */
    public static boolean isVariantLoaderPresent() {
        try {
            return hasVariantLoader(LoadingModList.get());
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Testable lookup: {@code null} or a failed {@code getModFileById} is absent.
     */
    public static boolean hasVariantLoader(LoadingModList loadingModList) {
        if (loadingModList == null) {
            return false;
        }
        try {
            return loadingModList.getModFileById(VARIANT_LOADER_MOD_ID) != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    @Override
    public void onLoad(String mixinPackage) {
        // Detection is deferred to shouldApplyMixin so tests can use the helper without Forge.
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return shouldApplyMixin(mixinClassName, isVariantLoaderPresent());
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
