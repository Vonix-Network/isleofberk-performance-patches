package network.vonix.isleofberkperformance.verification;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import network.vonix.isleofberkperformance.IsleOfBerkPerformanceMixinPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

/**
 * Proves the Variant Loader overlap skip set is exactly the 13 model-resource Mixins,
 * that those names are skipped only when the dependency is present, that every other
 * declared Mixin still applies, and that a null/uninitialized LoadingModList fails
 * closed without throwing. Does not require a live Forge client.
 */
public final class MixinPluginFixture {
    private static final String MIXIN_PACKAGE = "network.vonix.isleofberkperformance.mixin.";
    private static final String PLUGIN_CLASS =
            "network.vonix.isleofberkperformance.IsleOfBerkPerformanceMixinPlugin";
    private static final Pattern STRING = Pattern.compile("\"([A-Za-z0-9_]+)\"");

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
                "plugin skip set must equal the exact 13 model-resource Mixins: "
                        + IsleOfBerkPerformanceMixinPlugin.VARIANT_LOADER_OVERLAP_MIXINS);
        require("iobvariantloader".equals(IsleOfBerkPerformanceMixinPlugin.VARIANT_LOADER_MOD_ID),
                "plugin must detect Variant Loader by mod id iobvariantloader");

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
        }

        for (String name : EXPECTED_OVERLAP) {
            require(IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(name, true) == false,
                    "overlap mixin must skip when iobvariantloader is present: " + name);
            require(IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(MIXIN_PACKAGE + name, true) == false,
                    "fully-qualified overlap mixin must skip when present: " + name);
            require(IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(name, false),
                    "overlap mixin must apply when iobvariantloader is absent: " + name);
            require(IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(MIXIN_PACKAGE + name, false),
                    "fully-qualified overlap mixin must apply when absent: " + name);
        }

        for (String name : declared) {
            require(IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(name, false),
                    "declared mixin must apply when iobvariantloader is absent: " + name);
            boolean applyWithLoader = IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(name, true);
            if (EXPECTED_OVERLAP.contains(name)) {
                require(!applyWithLoader, "declared overlap mixin was not skipped: " + name);
            } else {
                require(applyWithLoader, "non-overlap mixin must not skip when iobvariantloader is present: " + name);
            }
        }

        for (String name : MUST_REMAIN_WITH_VARIANT_LOADER) {
            require(declared.contains(name), "non-overlap mixin missing from mixins.json: " + name);
            require(!EXPECTED_OVERLAP.contains(name), "non-overlap mixin listed in skip set: " + name);
            require(IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(name, true),
                    "glow/renderer/egg/saddle/other mixin must stay applied with Variant Loader: " + name);
        }

        int skipped = 0;
        for (String name : declared) {
            if (!IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(name, true)) {
                skipped++;
            }
        }
        require(skipped == 13, "exactly 13 declared Mixins must skip when Variant Loader is present, got " + skipped);

        require(!IsleOfBerkPerformanceMixinPlugin.hasVariantLoader(null),
                "null LoadingModList must fail closed (Variant Loader absent)");

        boolean present;
        try {
            present = IsleOfBerkPerformanceMixinPlugin.isVariantLoaderPresent();
        } catch (Throwable thrown) {
            throw new AssertionError(
                    "isVariantLoaderPresent must not throw when FML/LoadingModList is uninitialized: " + thrown,
                    thrown
            );
        }
        require(!present, "uninitialized LoadingModList.get() must fail closed (Variant Loader absent)");

        IMixinConfigPlugin plugin = new IsleOfBerkPerformanceMixinPlugin();
        for (String name : declared) {
            boolean applied;
            try {
                applied = plugin.shouldApplyMixin("unused-target", MIXIN_PACKAGE + name);
            } catch (Throwable thrown) {
                throw new AssertionError(
                        "plugin shouldApplyMixin must not throw when FML is uninitialized: " + name + ": " + thrown,
                        thrown
                );
            }
            require(applied, "uninitialized FML must apply mixin (fail closed / no-Variant-Loader): " + name);
        }

        System.out.println(
                "MixinPluginFixture: PASS (exact 13-name skip when iobvariantloader present; "
                        + "no skip when absent or LoadingModList uninitialized/null)"
        );
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
        Matcher matcher = STRING.matcher(json.substring(open, close));
        List<String> values = new ArrayList<>();
        while (matcher.find()) {
            values.add(matcher.group(1));
        }
        return values;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
