package network.vonix.isleofberkperformance.verification;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;

import network.vonix.isleofberkperformance.IsleOfBerkPerformanceMixinPlugin;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

/**
 * Deterministically proves the Variant Loader gate policy and PassengerLayer target fingerprint
 * contract without relying on ambient Forge startup state. If -DvariantLoaderJar is supplied,
 * the exact 2.7.0 class entry is also checked byte-for-byte.
 */
public final class MixinPluginFixture {
    private static final String MIXIN_PACKAGE = "network.vonix.isleofberkperformance.mixin.";
    private static final String PLUGIN_CLASS =
            "network.vonix.isleofberkperformance.IsleOfBerkPerformanceMixinPlugin";
    private static final String PASSENGER_MIXIN = "PassengerLayerMixin";
    private static final String STRUCTURE_CACHE_MIXIN = "StructureManagerCacheMixin";

    private static final Set<String> EXPECTED_OVERLAP = Set.of(
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

    private static final List<String> MUST_REMAIN_WITH_VARIANT_LOADER = List.of(
            "NightFuryGlowLayerResourceMixin",
            "LightFuryGlowLayerResourceMixin",
            "BaseSaddleAndChestsLayerCacheMixin",
            "SmallEggModelMixin",
            "MediumEggModelMixin",
            "LargeEggModelMixin",
            "ADragonEggBaseMixin",
            "GronckleRenderMixin",
            "NightFuryRenderMixin",
            "IoBParticleRenderMixin",
            "FlyNodeEvaluatorMixin",
            "WrappedGoalMixin",
            "ClientPacketHandlerClassMixin",
            "AnimationProcessorBoneCacheMixin"
    );

    private MixinPluginFixture() {}

    public static void main(String[] args) throws Exception {
        require(EXPECTED_OVERLAP.size() == 13, "expected overlap set must contain exactly 13 names");
        require(IsleOfBerkPerformanceMixinPlugin.VARIANT_LOADER_OVERLAP_MIXINS.equals(EXPECTED_OVERLAP),
                "plugin skip set must equal the exact 13 model-resource Mixins");
        require("iobvariantloader".equals(IsleOfBerkPerformanceMixinPlugin.VARIANT_LOADER_MOD_ID),
                "plugin must detect Variant Loader by mod id iobvariantloader");
        require(IsleOfBerkPerformanceMixinPlugin.EXACT_VARIANT_LOADER_VERSION.equals("2.7.0"),
                "plugin must retain exact Variant Loader 2.7.0 gating");
        require(IsleOfBerkPerformanceMixinPlugin.PASSENGER_LAYER_TARGET_DESCRIPTOR.contains("GeoEntityRenderer"),
                "passenger target descriptor must remain explicit");
        require(IsleOfBerkPerformanceMixinPlugin.isStructureManagerCacheMixin(STRUCTURE_CACHE_MIXIN),
                "structure cache mixin name must remain explicit");

        Class<?> pluginType = Class.forName(PLUGIN_CLASS);
        require(IMixinConfigPlugin.class.isAssignableFrom(pluginType),
                "plugin must implement IMixinConfigPlugin");
        require(!pluginType.getPackageName().endsWith(".mixin"),
                "plugin must live outside the mixin package: " + pluginType.getPackageName());

        String mixinsJson = Files.readString(Path.of("src/main/resources/isleofberkperformance.mixins.json"));
        require(mixinsJson.contains("\"plugin\": \"" + PLUGIN_CLASS + "\""),
                "mixins.json must wire " + PLUGIN_CLASS);
        require(!mixinsJson.contains("\"require\": 0") && !mixinsJson.contains("\"require\":0"),
                "mixins.json must not lower injector require to 0");

        Set<String> declared = declaredMixins(mixinsJson);
        for (String name : EXPECTED_OVERLAP) {
            require(declared.contains(name), "overlap mixin must remain declared: " + name);
            require(!IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                            name, true, IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.EXACT_2_7_0, false),
                    "overlap mixin must skip for exact Variant Loader 2.7.0: " + name);
            require(IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                            name, true, IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.ABSENT, false),
                    "overlap mixin must apply when Variant Loader is absent: " + name);
            require(!IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                            name, true, IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.MISMATCHED, false),
                    "overlap mixin must skip for any present Variant Loader version: " + name);
            require(!IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                            name, true, IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.MALFORMED, false),
                    "overlap mixin must fail closed when Variant Loader is present but metadata is malformed: " + name);
        }

        for (String name : declared) {
            boolean applyWithoutLoader = IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                    name, false, IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.ABSENT, false);
            require(applyWithoutLoader || PASSENGER_MIXIN.equals(name) || STRUCTURE_CACHE_MIXIN.equals(name),
                    "ordinary declared mixins must apply without Variant Loader: " + name);
            boolean applyWithExactLoader = IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                    name, true, IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.EXACT_2_7_0, false);
            if (EXPECTED_OVERLAP.contains(name)) {
                require(!applyWithExactLoader, "declared overlap mixin was not skipped: " + name);
            } else if (PASSENGER_MIXIN.equals(name)) {
                require(!applyWithExactLoader, "passenger guard must remain off without fingerprint: " + name);
            } else if (STRUCTURE_CACHE_MIXIN.equals(name)) {
                require(!applyWithExactLoader, "structure cache candidate must remain server-only: " + name);
            } else {
                require(applyWithExactLoader, "non-overlap mixin must remain active with Variant Loader: " + name);
            }
        }

        require(IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                        PASSENGER_MIXIN, true,
                        IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.EXACT_2_7_0, true),
                "passenger guard must apply only for exact metadata, client dist, and valid fingerprint");
        require(!IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                        PASSENGER_MIXIN, true,
                        IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.EXACT_2_7_0, false),
                "passenger guard must fail closed when fingerprint fails");
        require(!IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                        PASSENGER_MIXIN, false,
                        IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.EXACT_2_7_0, true),
                "passenger guard must remain client-only");
        require(!IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                        PASSENGER_MIXIN, true,
                        IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.MISMATCHED, true),
                "passenger guard must fail closed for mismatched versions");
        require(!IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                        PASSENGER_MIXIN, true,
                        IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.MALFORMED, true),
                "passenger guard must fail closed for malformed metadata");
        require(IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                        STRUCTURE_CACHE_MIXIN, false,
                        IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.ABSENT, false),
                "structure cache candidate must apply on the dedicated-server side");
        require(!IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                        STRUCTURE_CACHE_MIXIN, true,
                        IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.ABSENT, false),
                "structure cache candidate must remain off on the client side");

        require(IsleOfBerkPerformanceMixinPlugin.hasPassengerLayerMethodShape(
                        syntheticPassengerClass(true, IsleOfBerkPerformanceMixinPlugin.PASSENGER_LAYER_TARGET_DESCRIPTOR)),
                "synthetic exact static PassengerLayer method shape must pass");
        require(!IsleOfBerkPerformanceMixinPlugin.hasPassengerLayerMethodShape(
                        syntheticPassengerClass(false, IsleOfBerkPerformanceMixinPlugin.PASSENGER_LAYER_TARGET_DESCRIPTOR)),
                "instance PassengerLayer method shape must fail closed");
        require(!IsleOfBerkPerformanceMixinPlugin.hasPassengerLayerMethodShape(
                        syntheticPassengerClass(true, "()V")),
                "wrong PassengerLayer descriptor must fail closed");
        require(!IsleOfBerkPerformanceMixinPlugin.isExactPassengerLayerClass(
                        syntheticPassengerClass(true, IsleOfBerkPerformanceMixinPlugin.PASSENGER_LAYER_TARGET_DESCRIPTOR)),
                "synthetic bytes must not satisfy the exact 2.7.0 class fingerprint");

        String variantLoaderJar = System.getProperty("variantLoaderJar");
        if (variantLoaderJar != null && !variantLoaderJar.isEmpty()) {
            try (ZipFile zip = new ZipFile(variantLoaderJar)) {
                require(zip.getEntry(IsleOfBerkPerformanceMixinPlugin.PASSENGER_LAYER_TARGET_CLASS_ENTRY) != null,
                        "configured Variant Loader JAR must contain PassengerLayer.class");
                byte[] bytes = zip.getInputStream(
                        zip.getEntry(IsleOfBerkPerformanceMixinPlugin.PASSENGER_LAYER_TARGET_CLASS_ENTRY)).readAllBytes();
                require(IsleOfBerkPerformanceMixinPlugin.isExactPassengerLayerClass(bytes),
                        "configured Variant Loader JAR must match the exact 2.7.0 PassengerLayer fingerprint");
            }
        }

        require(!IsleOfBerkPerformanceMixinPlugin.hasVariantLoader(null),
                "null LoadingModList must fail closed (Variant Loader absent)");
        require(!IsleOfBerkPerformanceMixinPlugin.hasPassengerLayerTargetFingerprint(null),
                "null LoadingModList must fail closed (passenger fingerprint invalid)");

        IMixinConfigPlugin plugin = new IsleOfBerkPerformanceMixinPlugin();
        for (String name : declared) {
            boolean applied;
            try {
                applied = plugin.shouldApplyMixin("unused-target", MIXIN_PACKAGE + name);
            } catch (Throwable thrown) {
                throw new AssertionError("plugin shouldApplyMixin must never throw during uninitialized FML: " + name, thrown);
            }
            if (PASSENGER_MIXIN.equals(name)) {
                require(!applied, "uninitialized FML must disable the optional passenger guard");
            } else {
                require(applied, "uninitialized FML must leave ordinary Mixins on the no-Variant-Loader path: " + name);
            }
        }

        System.out.println(
                "MixinPluginFixture: PASS (presence-based overlap policy; deterministic PassengerLayer shape/fingerprint gate; fail-closed optional guard)"
        );
    }

    private static byte[] syntheticPassengerClass(boolean isStatic, String descriptor) {
        ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC,
                IsleOfBerkPerformanceMixinPlugin.PASSENGER_LAYER_TARGET_CLASS.replace('.', '/'),
                null, "java/lang/Object", null);
        writer.visitMethod(Opcodes.ACC_PUBLIC | (isStatic ? Opcodes.ACC_STATIC : 0),
                IsleOfBerkPerformanceMixinPlugin.PASSENGER_LAYER_TARGET_METHOD,
                descriptor, null, null).visitEnd();
        writer.visitEnd();
        return writer.toByteArray();
    }

    private static Set<String> declaredMixins(String json) {
        Set<String> names = new LinkedHashSet<>();
        names.addAll(stringArray(json, "mixins"));
        names.addAll(stringArray(json, "client"));
        require(!names.isEmpty(), "mixins.json declared no mixin classes");
        return names;
    }

    private static List<String> stringArray(String json, String key) {
        int start = json.indexOf('"' + key + '"');
        require(start >= 0, "mixins.json missing " + key);
        int open = json.indexOf('[', start);
        int close = json.indexOf(']', open);
        require(open >= 0 && close > open, "mixins.json " + key + " array is malformed");
        List<String> values = new ArrayList<>();
        String body = json.substring(open, close);
        int cursor = 0;
        while ((cursor = body.indexOf('"', cursor)) >= 0) {
            int end = body.indexOf('"', cursor + 1);
            require(end > cursor, "unterminated mixin name in " + key);
            values.add(body.substring(cursor + 1, end));
            cursor = end + 1;
        }
        return values;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
