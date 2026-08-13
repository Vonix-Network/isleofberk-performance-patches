package network.vonix.isleofberkperformance.verification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Exact bytecode gate for renderer substitutions and fixed-resource model methods. */
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

    private static final List<FixedResource> FIXED_RESOURCES = List.of(
            fixed("com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadderModel", "getModelLocation", "com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadder", "geo/dragons/deadly_nadder.geo.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadderModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.dragons.deadlynadder.DeadlyNadder", "animations/dragons/deadly_nadder.animation.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.gronckle.GronckleModel", "getModelLocation", "com.GACMD.isleofberk.entity.dragons.gronckle.Gronckle", "geo/dragons/gronckle.geo.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.gronckle.GronckleModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.dragons.gronckle.Gronckle", "animations/dragons/gronckle.animation.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.lightfury.LightFuryModel", "getModelLocation", "com.GACMD.isleofberk.entity.dragons.lightfury.LightFury", "geo/dragons/light_fury.geo.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.lightfury.LightFuryModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.dragons.lightfury.LightFury", "animations/dragons/light_fury.animation.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.montrous_nightmare.MonstrousNightmareModel", "getModelLocation", "com.GACMD.isleofberk.entity.dragons.montrous_nightmare.MonstrousNightmare", "geo/dragons/nightmare.geo.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.montrous_nightmare.MonstrousNightmareModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.dragons.montrous_nightmare.MonstrousNightmare", "animations/dragons/nightmare.animation.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.nightfury.NightFuryModel", "getModelLocation", "com.GACMD.isleofberk.entity.dragons.nightfury.NightFury", "geo/dragons/night_fury.geo.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.nightfury.NightFuryModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.dragons.nightfury.NightFury", "animations/dragons/night_fury.animation.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.nightlight.NightLightModel", "getModelLocation", "com.GACMD.isleofberk.entity.dragons.nightlight.NightLight", "geo/dragons/night_light.geo.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.nightlight.NightLightModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.dragons.nightlight.NightLight", "animations/dragons/night_fury.animation.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.skrill.SkrillModel", "getModelLocation", "com.GACMD.isleofberk.entity.dragons.skrill.Skrill", "geo/dragons/skrill.geo.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.skrill.SkrillModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.dragons.skrill.Skrill", "animations/dragons/skrill.animation.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStingerModel", "getModelLocation", "com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStinger", "geo/dragons/speed_stinger.geo.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStingerModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.dragons.speedstinger.SpeedStinger", "animations/dragons/speed_stinger.animation.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.speedstingerleader.SpeedStingerLeaderModel", "getModelLocation", "com.GACMD.isleofberk.entity.dragons.speedstingerleader.SpeedStingerLeader", "geo/dragons/speed_stinger.geo.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.speedstingerleader.SpeedStingerLeaderModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.dragons.speedstingerleader.SpeedStingerLeader", "animations/dragons/speed_stinger.animation.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.stinger.StingerModel", "getModelLocation", "com.GACMD.isleofberk.entity.dragons.stinger.Stinger", "geo/dragons/stinger.geo.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.stinger.StingerModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.dragons.stinger.Stinger", "animations/dragons/stinger.animation.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.terrible_terror.TerribleTerrorModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.dragons.terrible_terror.TerribleTerror", "animations/dragons/terrible_terror.animation.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.triple_stryke.TripleStrykeModel", "getModelLocation", "com.GACMD.isleofberk.entity.dragons.triple_stryke.TripleStryke", "geo/dragons/triple_stryke.geo.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.triple_stryke.TripleStrykeModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.dragons.triple_stryke.TripleStryke", "animations/dragons/triple_stryke.animation.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.zippleback.ZippleBackModel", "getModelLocation", "com.GACMD.isleofberk.entity.dragons.zippleback.ZippleBack", "geo/dragons/zippleback.geo.json"),
            fixed("com.GACMD.isleofberk.entity.dragons.zippleback.ZippleBackModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.dragons.zippleback.ZippleBack", "animations/dragons/zippleback.animation.json"),
            fixed("com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBoltModel", "getModelLocation", "com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBolt", "geo/projectile/projectile.medium.geo.json"),
            fixed("com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBoltModel", "getTextureLocation", "com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBolt", "textures/projectile/fireball.png"),
            fixed("com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBoltModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.projectile.proj_user.fire_bolt.FireBolt", "animations/projectile/projectile.medium.animation.json"),
            fixed("com.GACMD.isleofberk.entity.projectile.proj_user.furybolt.FuryBoltModel", "getModelLocation", "com.GACMD.isleofberk.entity.projectile.proj_user.furybolt.FuryBolt", "geo/projectile/fury.bolt.geo.json"),
            fixed("com.GACMD.isleofberk.entity.projectile.proj_user.furybolt.FuryBoltModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.projectile.proj_user.furybolt.FuryBolt", "animations/projectile/fury_bolt.animation.json"),
            fixed("com.GACMD.isleofberk.entity.eggs.entity.base.small.SmallEggModel", "getModelLocation", "com.GACMD.isleofberk.entity.eggs.entity.base.small.ADragonSmallEggBase", "geo/egg/small_egg_model.geo.json"),
            fixed("com.GACMD.isleofberk.entity.eggs.entity.base.small.SmallEggModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.eggs.entity.base.small.ADragonSmallEggBase", "animations/dragons/nightfury.animation.json"),
            fixed("com.GACMD.isleofberk.entity.eggs.entity.base.medium.MediumEggModel", "getModelLocation", "com.GACMD.isleofberk.entity.eggs.entity.base.medium.ADragonMediumEggBase", "geo/egg/medium_egg_model.geo.json"),
            fixed("com.GACMD.isleofberk.entity.eggs.entity.base.medium.MediumEggModel", "getAnimationFileLocation", "com.GACMD.isleofberk.entity.eggs.entity.base.medium.ADragonMediumEggBase", "animations/egg/nightfury.animation.json"),
            fixed("com.GACMD.isleofberk.entity.eggs.entity.base.large.ADragonLargeEggModel", "getModelLocation", "com.GACMD.isleofberk.entity.eggs.entity.base.large.ADragonLargeEggBase", "geo/egg/large_egg_model.geo.json"),
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
