package network.vonix.isleofberkperformance;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * Skips the 13 dragon model-resource Mixins when {@code iobvariantloader} is installed.
 * Variant Loader replaces the same resource methods, so the {@code NEW ResourceLocation}
 * Redirects have no injection point. Other Mixins stay applied.
 */
public final class IsleOfBerkPerformanceMixinPlugin implements IMixinConfigPlugin {
    public static final String VARIANT_LOADER_MOD_ID = "iobvariantloader";
    public static final String EXACT_VARIANT_LOADER_VERSION = "2.7.0";
    public static final String PASSENGER_LAYER_MIXIN_SIMPLE_NAME = "PassengerLayerMixin";
    public static final String STRUCTURE_MANAGER_CACHE_MIXIN_SIMPLE_NAME = "StructureManagerCacheMixin";
    public static final String PASSENGER_LAYER_TARGET_CLASS =
            "nordmods.iobvariantloader.util.layer.PassengerLayer";
    public static final String PASSENGER_LAYER_TARGET_CLASS_ENTRY =
            "nordmods/iobvariantloader/util/layer/PassengerLayer.class";
    public static final String PASSENGER_LAYER_TARGET_METHOD = "renderPassenger";
    public static final String PASSENGER_LAYER_TARGET_DESCRIPTOR =
            "(Lcom/GACMD/isleofberk/entity/base/dragon/ADragonBase;"
                    + "Lsoftware/bernie/geckolib3/model/AnimatedGeoModel;"
                    + "Lsoftware/bernie/geckolib3/geo/render/built/GeoBone;"
                    + "Lcom/mojang/blaze3d/vertex/PoseStack;"
                    + "Lcom/mojang/blaze3d/vertex/VertexConsumer;"
                    + "Lsoftware/bernie/geckolib3/renderers/geo/GeoEntityRenderer;I)V";
    /** Exact SHA-256 of the PassengerLayer.class entry in Variant Loader 2.7.0. */
    public static final String PASSENGER_LAYER_TARGET_CLASS_SHA256 =
            "6ef21fe66bc575448159fead00d974d7accf90c48e718c26c56e793eb63595a7";

    public enum VariantLoaderMatch {
        ABSENT,
        MALFORMED,
        MISMATCHED,
        EXACT_2_7_0
    }

    public static final class ModMetadata {
        public final String modId;
        public final String version;

        public ModMetadata(String modId, String version) {
            this.modId = modId;
            this.version = version;
        }
    }

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

    public static boolean isPassengerLayerMixin(String mixinClassName) {
        return PASSENGER_LAYER_MIXIN_SIMPLE_NAME.equals(simpleMixinName(mixinClassName));
    }

    public static boolean isStructureManagerCacheMixin(String mixinClassName) {
        return STRUCTURE_MANAGER_CACHE_MIXIN_SIMPLE_NAME.equals(simpleMixinName(mixinClassName));
    }

    public static VariantLoaderMatch classifyDeclaredVersion(String version) {
        if (version == null || version.isEmpty()) {
            return VariantLoaderMatch.MALFORMED;
        }
        return EXACT_VARIANT_LOADER_VERSION.equals(version)
                ? VariantLoaderMatch.EXACT_2_7_0
                : VariantLoaderMatch.MISMATCHED;
    }

    public static VariantLoaderMatch classifyMetadata(ModMetadata metadata) {
        if (metadata == null) {
            return VariantLoaderMatch.ABSENT;
        }
        if (metadata.modId == null || metadata.modId.isEmpty()) {
            return VariantLoaderMatch.MALFORMED;
        }
        if (!VARIANT_LOADER_MOD_ID.equals(metadata.modId)) {
            return VariantLoaderMatch.ABSENT;
        }
        return classifyDeclaredVersion(metadata.version);
    }

    public static VariantLoaderMatch classifyMetadataList(List<ModMetadata> mods) {
        if (mods == null) {
            return VariantLoaderMatch.MALFORMED;
        }
        ModMetadata found = null;
        for (ModMetadata metadata : mods) {
            if (metadata == null) {
                return VariantLoaderMatch.MALFORMED;
            }
            if (VARIANT_LOADER_MOD_ID.equals(metadata.modId)) {
                if (found != null) {
                    return VariantLoaderMatch.MALFORMED;
                }
                found = metadata;
            }
        }
        return classifyMetadata(found);
    }

    public static VariantLoaderMatch classify(LoadingModList loadingModList) {
        if (loadingModList == null) {
            return VariantLoaderMatch.ABSENT;
        }
        try {
            ModFileInfo file = loadingModList.getModFileById(VARIANT_LOADER_MOD_ID);
            if (file == null || file.getMods() == null) {
                return file == null ? VariantLoaderMatch.ABSENT : VariantLoaderMatch.MALFORMED;
            }
            List<ModMetadata> metadata = new ArrayList<>();
            for (IModInfo info : file.getMods()) {
                if (info == null) {
                    return VariantLoaderMatch.MALFORMED;
                }
                metadata.add(new ModMetadata(
                        info.getModId(), info.getVersion() == null ? null : info.getVersion().toString()));
            }
            return classifyMetadataList(metadata);
        } catch (Throwable ignored) {
            return VariantLoaderMatch.MALFORMED;
        }
    }

    public static VariantLoaderMatch classifyRuntime() {
        try {
            return classify(LoadingModList.get());
        } catch (Throwable ignored) {
            return VariantLoaderMatch.MALFORMED;
        }
    }

    public static boolean isClientDist(Dist dist) {
        return dist == Dist.CLIENT;
    }

    public static boolean isClientDist() {
        try {
            return isClientDist(FMLEnvironment.dist);
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Combined gate: skip resource-overlap Mixins for any present Variant Loader, and apply the
     * passenger Mixin only on the client with exact Variant Loader 2.7.0 metadata.
     */
    public static boolean shouldApplyMixin(
            String mixinClassName, boolean clientDist, VariantLoaderMatch match) {
        return shouldApplyMixin(mixinClassName, clientDist, match, false);
    }

    /**
     * Combined gate with an independently computed target fingerprint. The thirteen resource
     * Mixins are skipped for any present Variant Loader metadata. The passenger Mixin also
     * requires exact 2.7.0 metadata, target class fingerprint, and static method shape.
     */
    public static boolean shouldApplyMixin(
            String mixinClassName,
            boolean clientDist,
            VariantLoaderMatch match,
            boolean passengerTargetFingerprintValid) {
        if (isStructureManagerCacheMixin(mixinClassName)) {
            return !clientDist;
        }
        if (isPassengerLayerMixin(mixinClassName)) {
            return clientDist
                    && match == VariantLoaderMatch.EXACT_2_7_0
                    && passengerTargetFingerprintValid;
        }
        return match != VariantLoaderMatch.ABSENT
                && VARIANT_LOADER_OVERLAP_MIXINS.contains(simpleMixinName(mixinClassName))
                ? false
                : true;
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

    /**
     * Runtime structural fingerprint for the optional PassengerLayer target. This checks the
     * exact class entry bytes as well as the class name, method name, descriptor, and static flag.
     * Any missing file, changed bytecode, malformed class, or loader failure disables only the
     * optional passenger guard; it never throws into the render path.
     */
    public static boolean hasPassengerLayerTargetFingerprint(LoadingModList loadingModList) {
        if (loadingModList == null) {
            return false;
        }
        try {
            ModFileInfo file = loadingModList.getModFileById(VARIANT_LOADER_MOD_ID);
            if (file == null || file.getFile() == null || file.getFile().getFilePath() == null) {
                return false;
            }
            byte[] classBytes = readClassEntry(file.getFile().getFilePath());
            return isExactPassengerLayerClass(classBytes);
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** Testable byte-level fingerprint gate; does not require Forge runtime state. */
    public static boolean isExactPassengerLayerClass(byte[] classBytes) {
        if (classBytes == null) {
            return false;
        }
        try {
            if (!PASSENGER_LAYER_TARGET_CLASS_SHA256.equals(sha256Hex(classBytes))) {
                return false;
            }
            return hasPassengerLayerMethodShape(classBytes);
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** Structural-only helper used by deterministic fixtures for positive and negative shapes. */
    public static boolean hasPassengerLayerMethodShape(byte[] classBytes) {
        if (classBytes == null) {
            return false;
        }
        try {
            ClassNode node = new ClassNode();
            new ClassReader(classBytes).accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            if (!PASSENGER_LAYER_TARGET_CLASS.replace('.', '/').equals(node.name)) {
                return false;
            }
            for (MethodNode method : node.methods) {
                if (PASSENGER_LAYER_TARGET_METHOD.equals(method.name)
                        && PASSENGER_LAYER_TARGET_DESCRIPTOR.equals(method.desc)
                        && (method.access & Opcodes.ACC_STATIC) != 0) {
                    return true;
                }
            }
            return false;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean hasPassengerLayerTargetFingerprint() {
        try {
            return hasPassengerLayerTargetFingerprint(LoadingModList.get());
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static byte[] readClassEntry(Path filePath) throws IOException {
        if (Files.isDirectory(filePath)) {
            Path classFile = filePath.resolve(PASSENGER_LAYER_TARGET_CLASS_ENTRY);
            return Files.isRegularFile(classFile) ? Files.readAllBytes(classFile) : null;
        }
        try (ZipFile zip = new ZipFile(filePath.toFile())) {
            ZipEntry entry = zip.getEntry(PASSENGER_LAYER_TARGET_CLASS_ENTRY);
            if (entry == null) {
                return null;
            }
            try (InputStream stream = zip.getInputStream(entry)) {
                return stream.readAllBytes();
            }
        }
    }

    private static String sha256Hex(byte[] bytes) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
        StringBuilder result = new StringBuilder(digest.length * 2);
        for (byte value : digest) {
            result.append(String.format("%02x", value & 0xff));
        }
        return result.toString();
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
        VariantLoaderMatch match = classifyRuntime();
        boolean fingerprint = isPassengerLayerMixin(mixinClassName) && hasPassengerLayerTargetFingerprint();
        return shouldApplyMixin(mixinClassName, isClientDist(), match, fingerprint);
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
