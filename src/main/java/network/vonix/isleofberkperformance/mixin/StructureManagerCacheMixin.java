package network.vonix.isleofberkperformance.mixin;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Bounds the structure-template cache with an explicit size and write-expiry policy.
 *
 * <p>The lower Mixin priority lets a higher-priority replacement run first when present; this
 * companion then installs one deterministic cache owner without changing template parsing.
 */
@Mixin(value = StructureManager.class, priority = 900)
public abstract class StructureManagerCacheMixin {
    @Shadow
    @Final
    @Mutable
    private Map<ResourceLocation, Optional<StructureTemplate>> structureRepository;

    @Unique
    private Cache<ResourceLocation, Optional<StructureTemplate>> isleofberk$structureCache;

    @Inject(method = "<init>", at = @At("RETURN"), require = 1)
    private void isleofberk$boundStructureCache(
            ResourceManager resourceManager,
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            DataFixer dataFixer,
            CallbackInfo callbackInfo) {
        long maxStructureCacheEntries = PerformanceSettings.structureCacheMaxEntries();
        Cache<ResourceLocation, Optional<StructureTemplate>> boundedCache = CacheBuilder
                .<ResourceLocation, Optional<StructureTemplate>>newBuilder()
                .maximumSize(maxStructureCacheEntries)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .softValues()
                .build();
        this.isleofberk$structureCache = boundedCache;
        this.structureRepository = boundedCache.asMap();
    }

    @Inject(method = "onResourceManagerReload", at = @At("RETURN"), require = 1)
    private void isleofberk$cleanUpStructureCache(
            ResourceManager resourceManager,
            CallbackInfo callbackInfo) {
        this.isleofberk$structureCache.cleanUp();
    }
}
