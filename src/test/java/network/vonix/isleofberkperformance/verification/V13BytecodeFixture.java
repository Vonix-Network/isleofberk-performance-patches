package network.vonix.isleofberkperformance.verification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Exact original-jar counts for the V1.3 Redirect sites. Mixins must transform these
 * constructors/lookups rather than overwrite the enclosing methods.
 */
public final class V13BytecodeFixture {
    private static final List<String> PARTICLES = List.of(
            "com.GACMD.isleofberk.particles.FireBoltParticle",
            "com.GACMD.isleofberk.particles.FireCoatParticle",
            "com.GACMD.isleofberk.particles.FlameParticle",
            "com.GACMD.isleofberk.particles.FuryBoltParticle",
            "com.GACMD.isleofberk.particles.GasParticle",
            "com.GACMD.isleofberk.particles.SkrillLightningParticle",
            "com.GACMD.isleofberk.particles.SkrillSkillParticle"
    );

    private V13BytecodeFixture() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("usage: V13BytecodeFixture <original-isleofberk.jar>");
        }
        Path jar = Path.of(args[0]);
        if (!Files.isRegularFile(jar)) {
            throw new IOException("original dependency jar missing: " + jar);
        }

        String neighbors = methodBody(javap(jar, "com.GACMD.isleofberk.entity.base.path.FlyNodeEvaluator"), "m_6065_(");
        require(count(neighbors, "Maps.newEnumMap") == 1, "FlyNodeEvaluator.getNeighbors newEnumMap count\n" + neighbors);
        require(count(neighbors, "Map.put") == 1, "FlyNodeEvaluator.getNeighbors Map.put count\n" + neighbors);
        require(count(neighbors, "Map.get") == 2, "FlyNodeEvaluator.getNeighbors Map.get count\n" + neighbors);

        String pathType = methodBody(javap(jar, "com.GACMD.isleofberk.entity.base.path.FlyNodeEvaluator"), "m_7209_(");
        require(count(pathType, "MutableBlockPos.m_122178_") == 2,
                "FlyNodeEvaluator.getBlockPathType MutableBlockPos.set count\n" + pathType);

        for (String particle : PARTICLES) {
            String render = methodBody(javap(jar, particle), "m_5744_(");
            require(count(render, "Vector3f.\"<init>\":(FFF)V") == 4, particle + " Vector3f(FFF) count\n" + render);
            require(count(render, "Camera.m_90583_") == 3, particle + " Camera.getPosition count\n" + render);
        }

        String shock = methodBody(
                javap(jar, "com.GACMD.isleofberk.network.message.util.ClientPacketHandlerClass"),
                "handleSpawnShockParticles(");
        require(count(shock, "Minecraft.m_91087_") == 4, "handleSpawnShockParticles Minecraft.getInstance count\n" + shock);
        require(count(shock, "ClientLevel.m_6815_") == 1, "handleSpawnShockParticles ClientLevel.getEntity count\n" + shock);
        require(count(shock, "RegistryObject.get") == 1, "handleSpawnShockParticles RegistryObject.get count\n" + shock);
        require(count(shock, "Random.\"<init>\"") == 1, "handleSpawnShockParticles new Random count\n" + shock);

        String tame = methodBody(
                javap(jar, "com.GACMD.isleofberk.network.message.ClientMessageTameParticlesDragon"),
                "spawnTamingParticles(");
        require(count(tame, "m_21187_") == 3, "spawnTamingParticles getRandom count\n" + tame);

        System.out.println("V13BytecodeFixture: PASS (pathfinder EnumMap/set, 7 particle renders, packet lookups)");
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
        if (method < 0 || code < 0) {
            return "";
        }
        Matcher next = Pattern.compile("\\n  (public |protected |private |static |\\})").matcher(output);
        int end = next.find(code + 5) ? next.start() : output.length();
        return output.substring(method, end);
    }

    private static int count(String body, String needle) {
        int matches = 0;
        int from = 0;
        while (true) {
            int at = body.indexOf(needle, from);
            if (at < 0) {
                return matches;
            }
            matches++;
            from = at + needle.length();
        }
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
