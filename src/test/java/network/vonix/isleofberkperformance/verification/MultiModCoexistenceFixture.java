package network.vonix.isleofberkperformance.verification;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Structural multi-mod coexistence gate for the companion + digest-pinned Variant Loader
 * and deadlock-fix artifacts. Does not launch a client/server; records package/mixin
 * inventory evidence and fails closed if required external jars are missing or mismatched.
 */
public final class MultiModCoexistenceFixture {
    public static final String PINNED_VARIANT_LOADER_SHA256 =
            "666f47962912e332494b0331640cefad252d60fcf01d33fcc092ef0ac9a27f4a";
    public static final String PINNED_DEADLOCK_FIX_NAME_PREFIX = "isleofberk-deadlockfix";

    private MultiModCoexistenceFixture() {}

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            throw new IllegalArgumentException(
                    "usage: MultiModCoexistenceFixture <companion.jar> <variant-loader.jar> <deadlock-fix.jar>");
        }
        Path companion = Path.of(args[0]).toAbsolutePath().normalize();
        Path variant = Path.of(args[1]).toAbsolutePath().normalize();
        Path deadlock = Path.of(args[2]).toAbsolutePath().normalize();
        require(Files.isRegularFile(companion), "companion jar missing: " + companion);
        require(Files.isRegularFile(variant), "variant loader jar missing: " + variant);
        require(Files.isRegularFile(deadlock), "deadlock-fix jar missing: " + deadlock);

        String variantSha = sha256(variant);
        require(PINNED_VARIANT_LOADER_SHA256.equals(variantSha),
                "Variant Loader digest mismatch expected " + PINNED_VARIANT_LOADER_SHA256 + " got " + variantSha);

        Map<String, byte[]> companionEntries = readJar(companion);
        Map<String, byte[]> variantEntries = readJar(variant);
        Map<String, byte[]> deadlockEntries = readJar(deadlock);

        require(companionEntries.containsKey("isleofberkperformance.mixins.json"), "companion mixins.json missing");
        require(companionEntries.containsKey("network/vonix/isleofberkperformance/internal/AiMoveCadence.class"),
                "companion missing AiMoveCadence");
        require(companionEntries.containsKey("network/vonix/isleofberkperformance/internal/PerformanceSettings.class"),
                "companion missing PerformanceSettings");

        // Companion must not bundle IoB or deadlock-fix packages.
        for (String name : companionEntries.keySet()) {
            require(!name.startsWith("com/GACMD/isleofberk/"), "companion bundles IoB path " + name);
            require(!name.startsWith("network/vonix/isleofberkdeadlockfix/"), "companion bundles deadlock-fix path " + name);
        }

        // Deadlock-fix must remain a separate package surface.
        boolean deadlockMixin = deadlockEntries.keySet().stream().anyMatch(n -> n.endsWith(".mixins.json") || n.contains("deadlock"));
        require(deadlockMixin || deadlock.getFileName().toString().startsWith(PINNED_DEADLOCK_FIX_NAME_PREFIX),
                "deadlock-fix artifact identity not recognized");

        // Variant loader present as distinct artifact; no class path collision on companion package.
        for (String name : variantEntries.keySet()) {
            if (name.startsWith("network/vonix/isleofberkperformance/")) {
                throw new AssertionError("Variant Loader unexpectedly contains companion package class: " + name);
            }
        }
        for (String name : deadlockEntries.keySet()) {
            if (name.startsWith("network/vonix/isleofberkperformance/")) {
                throw new AssertionError("deadlock-fix unexpectedly contains companion package class: " + name);
            }
        }

        // Common mixin list retained; client renderer list size 12 + 5 fixed models.
        String mixinsJson = new String(companionEntries.get("isleofberkperformance.mixins.json"), StandardCharsets.UTF_8);
        require(mixinsJson.contains("WrappedGoalMixin"), "common WrappedGoalMixin required");
        require(mixinsJson.contains("DragonFlyAndAttackAirbourneTargetGoalMixin"), "common attack mixin required");
        require(mixinsJson.contains("DragonFollowPlayerFlyingMixin"), "common follow mixin required");
        require(mixinsJson.contains("UntamedDragonCircleFlightGoalMixin"), "common circle mixin required");
        require(mixinsJson.contains("ADragonEggBaseMixin"), "common egg mixin required");
        require(mixinsJson.contains("ShockEffectMixin"), "common shock mixin required");
        int clientRenderers = 0;
        for (String renderer : new String[] {
                "GronckleRenderMixin", "LightFuryRenderMixin", "MonstrousNightmareRenderMixin", "NightFuryRenderMixin",
                "NightLightRenderMixin", "SkrillRendererMixin", "SpeedStingerRenderMixin", "SpeedStingerLeaderRenderMixin",
                "StingerRenderMixin", "TerribleTerrorRenderMixin", "TripleStrykeRendererMixin", "ZippleBackRendererMixin"
        }) {
            if (mixinsJson.contains('"' + renderer + '"')) {
                clientRenderers++;
            }
        }
        require(clientRenderers == 12, "expected 12 client renderer mixins, got " + clientRenderers);

        System.out.println("MultiModCoexistenceFixture: PASS (structural)"
                + " companion=" + companion.getFileName()
                + " variantSha=" + variantSha
                + " deadlock=" + deadlock.getFileName()
                + " NOTE: client/server runtime launch evidence not executed by this fixture");
    }

    private static Map<String, byte[]> readJar(Path jar) throws IOException {
        Map<String, byte[]> map = new TreeMap<>();
        try (JarFile jf = new JarFile(jar.toFile())) {
            Enumeration<JarEntry> entries = jf.entries();
            while (entries.hasMoreElements()) {
                JarEntry e = entries.nextElement();
                if (e.isDirectory()) {
                    continue;
                }
                try (InputStream in = jf.getInputStream(e)) {
                    map.put(e.getName().replace('\\', '/'), in.readAllBytes());
                }
            }
        }
        return map;
    }

    private static String sha256(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) >= 0) {
                digest.update(buf, 0, n);
            }
        }
        return HexFormat.of().formatHex(digest.digest()).toLowerCase(Locale.ROOT);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
