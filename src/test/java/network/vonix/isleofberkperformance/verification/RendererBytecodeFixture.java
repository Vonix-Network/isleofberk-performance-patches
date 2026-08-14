package network.vonix.isleofberkperformance.verification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Exact bytecode gate for renderer substitutions and remaining constructor-constant
 * resource methods. Dragon geo/anim/texture lookups and egg geo/texture lookups are
 * remapped by Variant Loader and must not be treated as fixed resources.
 */
public final class RendererBytecodeFixture {
    private static final List<String> RENDERERS = List.of(
            "com.GACMD.isleofberk.entity.dragons.gronckle.GronckleRender",
            "com.GACMD.isleofberk.entity.dragons.lightfury.LightFuryRender",
            "com.GACMD.isleofberk.entity.dragons.montrous_nightmare.MonstrousNightmareRender",
            "com.GACMD.isleofberk.entity.dragons.nightfury.NightFuryRender",
            "com.GACMD.isleofberk.entity.dragons.nightlight.NightLightRender",
            "com.GACMD.isleofberk.entity.dragons.skrill.SkrillRenderer",
            "com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStingerRender",
            "com.GACMD.isleofberk.entity.dragons.speedstingerleader.SpeedStingerLeaderRender",
            "com.GACMD.isleofberk.entity.dragons.stinger.StingerRender",
            "com.GACMD.isleofberk.entity.dragons.terrible_terror.TerribleTerrorRender",
            "com.GACMD.isleofberk.entity.dragons.triple_stryke.TripleStrykeRenderer",
            "com.GACMD.isleofberk.entity.dragons.zippleback.ZippleBackRenderer"
    );

    private static final List<String> FORBIDDEN_DRAGON_MODEL_MIXINS = List.of(
            "DeadlyNadderModelMixin",
            "GronckleModelMixin",
            "LightFuryModelMixin",
            "MonstrousNightmareModelMixin",
            "NightFuryModelMixin",
            "NightLightModelMixin",
            "SkrillModelMixin",
            "SpeedStingerModelMixin",
            "SpeedStingerLeaderModelMixin",
            "StingerModelMixin",
            "TerribleTerrorModelMixin",
            "TripleStrykeModelMixin",
            "ZippleBackModelMixin"
    );

    private static final List<FixedResource> FIXED_RESOURCES = List.of(
            fixed("com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBoltModel", "getModelLocation", "com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBolt", "geo/projectile/projectile.medium.geo.json"),
            fixed("com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBoltModel", "getTextureLocation", "com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBolt", "textures/projectile/fireball.png"),
            fixed("com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBoltModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBolt", "animations/projectile/projectile.medium.animation.json"),
            fixed("com.GACMD.isleofberk.entity.projectile.proj_user.furybolt.FuryBoltModel", "getModelLocation", "com.GACMD.isleofberk.entity.projectile.proj_user.furybolt.FuryBolt", "geo/projectile/fury.bolt.geo.json"),
            fixed("com.GACMD.isleofberk.entity.projectile.proj_user.furybolt.FuryBoltModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.projectile.proj_user.furybolt.FuryBolt", "animations/projectile/fury_bolt.animation.json"),
            fixed("com.GACMD.isleofberk.entity.eggs.entity.base.small.SmallEggModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.eggs.entity.base.small.ADragonSmallEggBase", "animations/dragons/nightfury.animation.json"),
            fixed("com.GACMD.isleofberk.entity.eggs.entity.base.medium.MediumEggModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.eggs.entity.base.medium.ADragonMediumEggBase", "animations/egg/nightfury.animation.json"),
            fixed("com.GACMD.isleofberk.entity.eggs.entity.base.large.ADragonLargeEggModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.eggs.entity.base.large.ADragonLargeEggBase", "animations/egg/nightfury.animation.json")
    );

    private RendererBytecodeFixture() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("usage: RendererBytecodeFixture <original-isleofberk.jar>");
        }
        Path jar = Path.of(args[0]);
        if (!Files.isRegularFile(jar)) {
            throw new IOException("original dependency jar missing: " + jar);
        }
        rejectDragonModelMixins(Path.of("src/main/resources/isleofberkperformance.mixins.json"));
        rejectCancelledVariantLookups(Path.of("src/main/java/network/vonix/isleofberkperformance/mixin"));
        for (String renderer : RENDERERS) {
            String output = javap(jar, renderer);
            String body = methodBody(output, "getRenderType(");
            require(normalize(body).equals(
                    "aload_0\naload_1\ninvokevirtual getTextureLocation:(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/resources/ResourceLocation;\ninvokestatic RenderType.m_110458_:(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;\nareturn"),
                    renderer + " exact method body:\n" + normalize(body));
        }
        for (FixedResource resource : FIXED_RESOURCES) {
            String body = methodBody(javap(jar, resource.owner()), resource.method() + "(" + resource.parameter() + ");");
            String normalized = normalizeFixed(body);
            require(normalized.equals("new ResourceLocation isleofberk " + resource.path()),
                    resource.owner() + "#" + resource.method() + " exact method body:\n" + normalized);
        }
        System.out.println("RendererBytecodeFixture: PASS (" + RENDERERS.size() + " renderers, " + FIXED_RESOURCES.size() + " fixed-resource methods)");
    }

    private static void rejectDragonModelMixins(Path mixinsJson) throws IOException {
        require(Files.isRegularFile(mixinsJson), "companion mixin config missing: " + mixinsJson);
        String json = Files.readString(mixinsJson);
        for (String mixin : FORBIDDEN_DRAGON_MODEL_MIXINS) {
            require(!json.contains('"' + mixin + '"'), "Variant Loader remaps dragon geo/anim; " + mixin + " must not be registered");
        }
    }

    private static void rejectCancelledVariantLookups(Path mixinDir) throws IOException {
        require(Files.isDirectory(mixinDir), "companion mixin directory missing: " + mixinDir);
        try (var paths = Files.list(mixinDir)) {
            for (Path path : paths.toList()) {
                if (!path.getFileName().toString().endsWith(".java")) {
                    continue;
                }
                String source = Files.readString(path);
                boolean cancelsResource = source.contains("getModelLocation")
                        || source.contains("getAnimationFileLocation")
                        || source.contains("getTextureLocation");
                if (!cancelsResource) {
                    continue;
                }
                require(!source.contains("entity.dragons."),
                        path.getFileName() + " must not cancel dragon geo/anim/texture lookups remapped by Variant Loader");
                require(!source.contains("method = \"getModelLocation") || !source.contains("entity.eggs."),
                        path.getFileName() + " must not cancel egg getModelLocation remapped by Variant Loader");
                require(!source.contains("method = \"getTextureLocation") || !source.contains("entity.eggs."),
                        path.getFileName() + " must not cancel egg getTextureLocation remapped by Variant Loader");
            }
        }
    }

    private static FixedResource fixed(String owner, String method, String parameter, String path) {
        return new FixedResource(owner, method, parameter, path);
    }

    private static String javap(Path jar, String className) throws Exception {
        Process process = new ProcessBuilder("javap", "-classpath", jar.toAbsolutePath().toString(), "-p", "-c", className)
                .redirectErrorStream(true).start();
        String output;
        try (InputStream input = process.getInputStream()) {
            output = read(input);
        }
        if (process.waitFor() != 0) {
            throw new IOException("javap failed for " + className + ":\n" + output);
        }
        return output;
    }

    private static String methodBody(String output, String declarationFragment) {
        int method = output.indexOf(" " + declarationFragment);
        int code = output.indexOf("Code:", method);
        int end = output.indexOf("\n\n", code);
        return method >= 0 && code >= 0 && end >= 0 ? output.substring(method, end) : "";
    }

    private static String normalize(String body) {
        StringBuilder normalized = new StringBuilder();
        for (String line : body.split("\\R")) {
            String instruction = line.trim().replaceFirst("^\\d+:\\s*", "");
            if (instruction.equals(line.trim())) {
                continue;
            }
            if (instruction.contains("getTextureLocation:")) {
                normalized.append("invokevirtual ").append(instruction.substring(instruction.indexOf("getTextureLocation:"))).append('\n');
            } else if (instruction.contains("RenderType.m_110458_:")) {
                normalized.append("invokestatic ").append(instruction.substring(instruction.indexOf("RenderType.m_110458_:"))).append('\n');
            } else {
                int comment = instruction.indexOf(" //");
                normalized.append(comment >= 0 ? instruction.substring(0, comment) : instruction).append('\n');
            }
        }
        return normalized.toString().strip();
    }

    private static String normalizeFixed(String body) {
        StringBuilder normalized = new StringBuilder();
        for (String line : body.split("\\R")) {
            String instruction = line.trim().replaceFirst("^\\d+:\\s*", "");
            if (instruction.contains("new") && instruction.contains("ResourceLocation")) {
                normalized.append("new ResourceLocation ");
            } else if (instruction.contains("ldc") && instruction.contains("String isleofberk")) {
                normalized.append("isleofberk ");
            } else if (instruction.contains("ldc") && instruction.contains("String ")) {
                normalized.append(instruction.substring(instruction.indexOf("String ") + 7).strip()).append(' ');
            } else if (instruction.contains("invokespecial") && instruction.contains("ResourceLocation")) {
                normalized.append(' ');
            } else if (instruction.endsWith("areturn")) {
                normalized.append(' ');
            }
        }
        return normalized.toString().strip();
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

    private record FixedResource(String owner, String method, String parameter, String path) {}
}
