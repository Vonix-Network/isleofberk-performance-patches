package network.vonix.isleofberkperformance.verification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

/**
 * Every declared mixin has a class, every mixin class is declared, and V1.3 mixins stay
 * narrow Redirect/Inject rather than {@code @Overwrite} or cancelled render bodies.
 */
public final class MixinInventoryFixture {
    private static final String MIXIN_PACKAGE = "network.vonix.isleofberkperformance.mixin.";
    private static final Pattern STRING = Pattern.compile("\"([A-Za-z0-9_]+)\"");

    private MixinInventoryFixture() {}

    public static void main(String[] args) throws Exception {
        Path mixinsJson = Path.of("src/main/resources/isleofberkperformance.mixins.json");
        Path mixinDir = Path.of("src/main/java/network/vonix/isleofberkperformance/mixin");
        Set<String> declared = declaredMixins(Files.readString(mixinsJson));
        Set<String> sources = mixinSourceNames(mixinDir);

        for (String name : declared) {
            Class.forName(MIXIN_PACKAGE + name);
            require(sources.contains(name), "declared mixin is missing a source class: " + name);
        }
        for (String name : sources) {
            require(declared.contains(name), "mixin source is not declared in mixins.json: " + name);
        }

        try (var paths = Files.list(mixinDir)) {
            for (Path path : paths.toList()) {
                if (!path.getFileName().toString().endsWith(".java")) {
                    continue;
                }
                String source = Files.readString(path);
                require(!source.contains("@Overwrite"), path.getFileName() + " must not use @Overwrite");
            }
        }

        String particle = Files.readString(mixinDir.resolve("IoBParticleRenderMixin.java"));
        require(particle.contains("@Redirect"), "IoBParticleRenderMixin must redirect constructors/lookups");
        require(particle.contains("NEW"), "IoBParticleRenderMixin must target Vector3f constructors");
        require(!particle.contains("cancellable = true"), "IoBParticleRenderMixin must not cancel render");
        require(!particle.contains("setReturnValue"), "IoBParticleRenderMixin must not replace the render return");
        require(particle.contains("FireBoltParticle.class")
                        && particle.contains("FireCoatParticle.class")
                        && particle.contains("FlameParticle.class")
                        && particle.contains("FuryBoltParticle.class")
                        && particle.contains("GasParticle.class")
                        && particle.contains("SkrillLightningParticle.class")
                        && particle.contains("SkrillSkillParticle.class"),
                "IoBParticleRenderMixin must cover all seven particle families");

        String pathfinder = Files.readString(mixinDir.resolve("FlyNodeEvaluatorMixin.java"));
        require(pathfinder.contains("Maps;newEnumMap"), "FlyNodeEvaluatorMixin must redirect Maps.newEnumMap");
        require(pathfinder.contains("ordinal = 1"), "FlyNodeEvaluatorMixin must skip only the second MutableBlockPos.set");
        require(!pathfinder.contains("cancellable = true"), "FlyNodeEvaluatorMixin must not cancel getNeighbors");

        String packets = Files.readString(mixinDir.resolve("ClientPacketHandlerClassMixin.java"));
        require(packets.contains("Minecraft;getInstance()"), "ClientPacketHandlerClassMixin must reuse Minecraft.getInstance");
        require(packets.contains("getEntity(I)"), "ClientPacketHandlerClassMixin must reuse entity lookup");
        require(!packets.contains("cancellable = true"), "ClientPacketHandlerClassMixin must not replace the handler");

        String tame = Files.readString(mixinDir.resolve("ClientMessageTameParticlesDragonMixin.java"));
        require(tame.contains("ADragonBase;getRandom()"), "ClientMessageTameParticlesDragonMixin must reuse ADragonBase.getRandom");
        require(!tame.contains("cancellable = true"), "ClientMessageTameParticlesDragonMixin must not replace spawnTamingParticles");

        if (args.length == 1) {
            verifyPackaged(Path.of(args[0]), declared);
        }
        System.out.println("MixinInventoryFixture: PASS (" + declared.size() + " declared mixins, sources and packaged classes match)");
    }

    private static void verifyPackaged(Path jar, Set<String> declared) throws IOException {
        require(Files.isRegularFile(jar), "packaged JAR missing: " + jar);
        Set<String> packaged = new LinkedHashSet<>();
        try (ZipFile zip = new ZipFile(jar.toFile())) {
            zip.stream().forEach(entry -> {
                String name = entry.getName().replace('\\', '/');
                String prefix = "network/vonix/isleofberkperformance/mixin/";
                if (name.startsWith(prefix) && name.endsWith(".class") && !name.contains("$")) {
                    packaged.add(name.substring(prefix.length(), name.length() - 6));
                }
            });
        }
        for (String name : declared) {
            require(packaged.contains(name), "declared mixin is not packaged: " + name);
        }
        for (String name : packaged) {
            require(declared.contains(name), "packaged mixin class is not declared: " + name);
        }
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

    private static Set<String> mixinSourceNames(Path mixinDir) throws IOException {
        require(Files.isDirectory(mixinDir), "mixin directory missing: " + mixinDir);
        Set<String> names = new LinkedHashSet<>();
        try (var paths = Files.list(mixinDir)) {
            for (Path path : paths.toList()) {
                String file = path.getFileName().toString();
                if (file.endsWith(".java")) {
                    names.add(file.substring(0, file.length() - 5));
                }
            }
        }
        return names;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
