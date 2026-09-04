package network.vonix.isleofberkperformance.mixin;

import java.util.Map;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.LevelStorageSource;
import com.mojang.datafixers.DataFixer;
import network.vonix.isleofberkperformance.internal.PerformanceSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bounds the structure-template cache after ModernFix has installed its soft-value map.
 *
 * <p>The lower Mixin priority lets ModernFix perform its normal replacement first; this
 * candidate then adds a deterministic maximum-size bound without changing template parsing.
 */
@Mixin(value = StructureManager.class, priority = 900)
public abstract class StructureManagerCacheMixin {
    @Shadow
    @Final
    @Mutable
    private Map<ResourceLocation, StructureTemplate> structureRepository;

    @Inject(method = "<init>", at = @At("RETURN"), require = 1)
    private void isleofberk$boundStructureCache(
            ResourceManager resourceManager,
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            DataFixer dataFixer,
            CallbackInfo callbackInfo) {
        long maxStructureCacheEntries = PerformanceSettings.structureCacheMaxEntries();
        Cache<ResourceLocation, StructureTemplate> boundedCache = CacheBuilder.<ResourceLocation, StructureTemplate>newBuilder()
                .maximumSize(maxStructureCacheEntries)
                .softValues()
                .build();
        this.structureRepository = boundedCache.asMap();
    }
}
