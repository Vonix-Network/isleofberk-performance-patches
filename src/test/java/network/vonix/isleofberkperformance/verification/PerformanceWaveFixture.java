package network.vonix.isleofberkperformance.verification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

/**
 * Fail-closed descriptor and activation gate for the 2026-08-19 safe performance wave.
 * The scan methods and network/projectile paths are audited but intentionally untransformed:
 * this companion has no supported exact replacement mechanism for their large bodies.
 */
public final class PerformanceWaveFixture {
    private static final String PINNED_IOB_SHA256 =
            "a4b17befb1350d6d4cd07d7fdfcb2b3cec37a5c501e1f4fb811946f3e971dfc0";
    private static final Pattern BLOCK_POS_NEW = Pattern.compile("new\\s+#\\d+\\s+// class net/minecraft/core/BlockPos");
    private static final Pattern VEC3_ADD = Pattern.compile("Vec3\\.m_82520_:\\(DDD\\)Lnet/minecraft/world/phys/Vec3;");
    private static final Pattern MAP_SIZE = Pattern.compile("InterfaceMethod java/util/Map\\.size:\\(\\)I");

    private PerformanceWaveFixture() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            throw new IllegalArgumentException("usage: PerformanceWaveFixture <original-isleofberk.jar> <companion.jar> <wave-note.md>");
        }
        Path original = Path.of(args[0]).toAbsolutePath().normalize();
        Path companion = Path.of(args[1]).toAbsolutePath().normalize();
        Path note = Path.of(args[2]).toAbsolutePath().normalize();
        require(Files.isRegularFile(original), "original IoB jar missing: " + original);
        require(Files.isRegularFile(companion), "companion jar missing: " + companion);
        require(Files.isRegularFile(note), "wave note missing: " + note);
        require(PINNED_IOB_SHA256.equals(sha256(original)), "unexpected original IoB digest");

        verifyFollowMapLifecycleDescriptor(original);
        verifyUnchangedScanShapes(original);
        verifyProjectileAndFlapDescriptors(original);
        verifyBoundedLifecycleModel();
        verifyCompanionActivation(companion);
        verifyPackagedMixinBytecode(companion);
        verifyCandidateProvenance(note);

        System.out.println("PerformanceWaveFixture: PASS (IoB pin, follow stop cleanup, bounded formation map, unchanged scan shapes, activation, provenance)");
    }

    private static void verifyFollowMapLifecycleDescriptor(Path jar) throws Exception {
        String base = javap(jar, "com.GACMD.isleofberk.entity.AI.flight.ADragonBaseBaseFlyingRideableGoal");
        require(base.contains("protected java.util.Map<java.util.UUID, com.GACMD.isleofberk.entity.base.dragon.ADragonBaseFlyingRideable> tailingDragons;"),
                "tailingDragons field generic contract changed\n" + base);
        require(base.contains("descriptor: Ljava/util/Map;"), "tailingDragons descriptor must be Map\n" + base);
        require(base.contains("new           #17                 // class java/util/HashMap"),
                "base goal must retain its per-goal HashMap construction\n" + base);

        String follow = javap(jar, "com.GACMD.isleofberk.entity.AI.flight.player.DragonFollowPlayerFlying");
        String tick = methodBody(follow, "m_8037_();");
        String stop = methodBody(follow, "m_8041_();");
        require(count("InterfaceMethod java/util/Map.put", tick) == 1,
                "follow tick must retain one active formation map put\n" + tick);
        require(count(MAP_SIZE, tick) == 4,
                "follow tick must retain four formation-size reads\n" + tick);
        require(stop.contains("descriptor: ()V") && stop.contains("setIsDragonDisabled:(Z)V"),
                "follow stop descriptor/body changed\n" + stop);
    }

    private static void verifyUnchangedScanShapes(Path jar) throws Exception {
        String base = javap(jar, "com.GACMD.isleofberk.entity.base.dragon.ADragonBase");
        String air = methodBody(base, "airSpaceMechanics();");
        require(air.contains("descriptor: ()V"), "airSpaceMechanics descriptor changed");
        require(air.contains("iconst_2"), "airSpaceMechanics radius must remain 2");
        require(count(BLOCK_POS_NEW, air) == 4 && count(VEC3_ADD, air) == 4,
                "airSpaceMechanics must retain four ordered probe expressions per loop cell\n" + air);
        require(count("setIsDragonOnAirspaceClear:(Z)V", air) == 2,
                "airSpaceMechanics result precedence changed\n" + air);

        String rideable = javap(jar, "com.GACMD.isleofberk.entity.base.dragon.ADragonBaseFlyingRideable");
        String ground = methodBody(rideable, "onGroundMechanics();");
        require(ground.contains("descriptor: ()V"), "rideable onGroundMechanics descriptor changed");
        require(ground.contains("iconst_4"), "rideable ground scan radius must remain 4");
        require(count(BLOCK_POS_NEW, ground) == 4 && count(VEC3_ADD, ground) == 4,
                "rideable ground scan must retain four ordered probe expressions per loop cell\n" + ground);
        require(count("setIsDragonOnGround:(Z)V", ground) == 2,
                "rideable ground result precedence changed\n" + ground);
    }

    private static void verifyProjectileAndFlapDescriptors(Path jar) throws Exception {
        String projectile = javap(jar, "com.GACMD.isleofberk.entity.projectile.abase.BaseLinearFlightProjectile");
        require(projectile.contains("getLargerHitResultForEntityCollisions(net.minecraft.world.entity.Entity, java.util.function.Predicate<net.minecraft.world.entity.Entity>);"),
                "projectile collision-query descriptor changed\n" + projectile);
        require(projectile.contains("protected void callExplosionEffects(boolean, com.GACMD.isleofberk.entity.base.dragon.ADragonRideableUtility);"),
                "projectile explosion contract changed\n" + projectile);

        String flap = javap(jar, "com.GACMD.isleofberk.network.message.MessageDragonFlapSounds");
        require(flap.contains("public static void handle(com.GACMD.isleofberk.network.message.MessageDragonFlapSounds, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent$Context>);"),
                "flap packet handle descriptor changed\n" + flap);
        require(flap.contains("descriptor: (Lcom/GACMD/isleofberk/network/message/MessageDragonFlapSounds;Ljava/util/function/Supplier;)V"),
                "flap packet descriptor changed\n" + flap);
    }

    private static void verifyBoundedLifecycleModel() {
        Map<String, Object> tailing = new HashMap<>();
        Object dragon = new Object();
        for (int tick = 0; tick < 32; tick++) {
            tailing.put("active-owner", dragon);
            require(tailing.size() == 1, "active formation map must preserve a single owner slot");
        }
        tailing.clear();
        require(tailing.isEmpty(), "goal stop must release inactive formation references");
        tailing.put("active-owner", dragon);
        require(tailing.size() == 1, "first post-restart active tick must restore formation map semantics");
    }

    private static void verifyCompanionActivation(Path jar) throws Exception {
        try (ZipFile zip = new ZipFile(jar.toFile())) {
            String json = readZip(zip, "isleofberkperformance.mixins.json");
            require(json.contains("\"ADragonBaseBaseFlyingRideableGoalMixin\""), "base goal lifecycle mixin not active");
            require(json.contains("\"DragonFollowPlayerFlyingMixin\""), "follow mixin not active");
            require(!json.contains("ADragonBaseMixin") && !json.contains("ADragonBaseFlyingRideableMixin"),
                    "unsupported scan-method replacement mixin must not be active");
            require(!json.contains("BaseLinearFlightProjectileMixin") && !json.contains("MessageDragonFlapSoundsMixin"),
                    "projectile/flap path must remain unpatched without proof");
            require(zip.getEntry("network/vonix/isleofberkperformance/mixin/ADragonBaseBaseFlyingRideableGoalMixin.class") != null,
                    "packaged base lifecycle mixin missing");
            require(zip.getEntry("network/vonix/isleofberkperformance/internal/TailingDragonLifecycle.class") != null,
                    "packaged lifecycle interface missing");
            zip.stream().forEach(entry -> require(!entry.getName().startsWith("com/GACMD/isleofberk/"),
                    "companion bundles forbidden IoB class/resource: " + entry.getName()));
        }
    }

    private static void verifyCandidateProvenance(Path note) throws IOException {
        String text = Files.readString(note, StandardCharsets.UTF_8);
        for (String required : new String[] {
                "candidate-only", PINNED_IOB_SHA256, "P01", "implemented", "P02", "BLOCKED",
                "BaseLinearFlightProjectile", "BLOCKED", "MessageDragonFlapSounds", "DEFERRED",
                "no external effects"
        }) {
            require(text.contains(required), "wave note missing required provenance term: " + required);
        }
        require(!text.contains("accepted") && !text.contains("deployed") && !text.contains("published"),
                "wave note must not claim acceptance, deployment, or publication");
    }

    private static void verifyPackagedMixinBytecode(Path companion) throws Exception {
        String baseMixin = javap(companion,
                "network.vonix.isleofberkperformance.mixin.ADragonBaseBaseFlyingRideableGoalMixin");
        require(baseMixin.contains("tailingDragons") && baseMixin.contains("InterfaceMethod java/util/Map.clear:()V"),
                "base lifecycle mixin must clear only the pinned map\n" + baseMixin);
        require(!baseMixin.contains("static "), "base lifecycle mixin must not add a static retention cache\n" + baseMixin);

        String followMixin = javap(companion,
                "network.vonix.isleofberkperformance.mixin.DragonFollowPlayerFlyingMixin");
        require(followMixin.contains("vonix$clearTailingDragonsOnStop")
                        && followMixin.contains("TailingDragonLifecycle.vonix$clearTailingDragons:()V"),
                "follow mixin must invoke lifecycle cleanup from its exact stop handler\n" + followMixin);
    }

    private static String readZip(ZipFile zip, String entryName) throws IOException {
        var entry = zip.getEntry(entryName);
        if (entry == null) {
            throw new IOException("missing JAR entry " + entryName);
        }
        try (InputStream in = zip.getInputStream(entry)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static int count(String needle, String text) {
        int found = 0;
        for (int index = text.indexOf(needle); index >= 0; index = text.indexOf(needle, index + needle.length())) {
            found++;
        }
        return found;
    }

    private static int count(Pattern pattern, String text) {
        int found = 0;
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            found++;
        }
        return found;
    }

    private static String methodBody(String output, String declaration) {
        int method = output.indexOf(" " + declaration);
        int code = output.indexOf("Code:", method);
        if (method < 0 || code < 0) {
            return "";
        }
        Matcher next = Pattern.compile("\\n  (public |protected |private |static |\\})").matcher(output);
        int end = next.find(code + 5) ? next.start() : output.length();
        return output.substring(method, end);
    }

    private static String javap(Path jar, String className) throws Exception {
        Process process = new ProcessBuilder("javap", "-classpath", jar.toString(), "-p", "-c", "-s", className)
                .redirectErrorStream(true).start();
        String output;
        try (InputStream in = process.getInputStream()) {
            output = read(in);
        }
        if (process.waitFor() != 0) {
            throw new IOException("javap failed for " + className + ":\n" + output);
        }
        return output;
    }

    private static String sha256(Path file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream in = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            for (int count; (count = in.read(buffer)) >= 0; ) {
                digest.update(buffer, 0, count);
            }
        }
        return HexFormat.of().formatHex(digest.digest());
    }

    private static String read(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        input.transferTo(output);
        return output.toString(StandardCharsets.UTF_8);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
