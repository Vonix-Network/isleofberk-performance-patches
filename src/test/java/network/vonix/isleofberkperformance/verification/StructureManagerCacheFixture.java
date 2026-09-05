package network.vonix.isleofberkperformance.verification;

import java.nio.file.Files;
import java.nio.file.Path;

import network.vonix.isleofberkperformance.internal.PerformanceSettings;

/** Deterministic source-contract gate for the configurable server-side structure cache candidate. */
public final class StructureManagerCacheFixture {
    private StructureManagerCacheFixture() {}

    public static void main(String[] args) throws Exception {
        Path source = Path.of(
                "src/main/java/network/vonix/isleofberkperformance/mixin/StructureManagerCacheMixin.java");
        Path config = Path.of(
                "src/main/java/network/vonix/isleofberkperformance/config/PerformanceConfig.java");
        String text = Files.readString(source);
        String configText = Files.readString(config);
        require(text.contains("@Mixin(value = StructureManager.class, priority = 900)"),
                "candidate must run after ModernFix's default-priority constructor injection");
        require(text.contains("PerformanceSettings.structureCacheMaxEntries()"),
                "candidate must read the reload-correct configured cache bound");
        require(text.contains("Map<ResourceLocation, Optional<StructureTemplate>>"),
                "candidate must preserve the Forge 1.18.2 repository value contract");
        require(text.contains(".maximumSize(maxStructureCacheEntries)"),
                "candidate must configure the bounded cache from the snapshot");
        require(text.contains(".expireAfterWrite(30, TimeUnit.MINUTES)"),
                "candidate must bound stale entries with explicit write expiry");
        require(text.contains(".softValues()"),
                "candidate must preserve GC-sensitive soft-value behavior");
        require(text.contains("method = \"onResourceManagerReload\""),
                "candidate must attach cleanup to the target reload lifecycle");
        require(text.contains("isleofberk$structureCache.cleanUp()"),
                "candidate must drain expired/cleared cache entries after reload");
        require(text.contains("@Unique"),
                "candidate must retain the cache handle for explicit maintenance");
        require(text.contains("structureRepository"),
                "candidate must target the official-mapped Forge 1.18.2 StructureManager repository field");
        require(!text.contains("@Overwrite"), "candidate must not overwrite the full StructureManager lifecycle");
        require(configText.contains("defineInRange(\"structure_cache_max_entries\", 512, 64, 4096)"),
                "candidate must advertise the tested default and inclusive bounds");

        PerformanceSettings.overwrite(true, 4, 20, 8, 63);
        require(PerformanceSettings.structureCacheMaxEntries() == 64,
                "structure cache setting must clamp values below 64 to 64");
        PerformanceSettings.overwrite(true, 4, 20, 8, 4097);
        require(PerformanceSettings.structureCacheMaxEntries() == 4096,
                "structure cache setting must clamp values above 4096 to 4096");
        PerformanceSettings.overwrite(true, 4, 20, 8, 512);
        require(PerformanceSettings.structureCacheMaxEntries() == 512,
                "structure cache setting must restore the configured default");
        System.out.println("StructureManagerCacheFixture: PASS (configurable bounded soft-value cache, default/range/clamping)");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}