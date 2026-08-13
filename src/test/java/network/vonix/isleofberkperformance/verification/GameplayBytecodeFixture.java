package network.vonix.isleofberkperformance.verification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Exact bytecode counts for the common gameplay mixins against pinned Isle of Berk 1.2.0.
 */
public final class GameplayBytecodeFixture {
    private static final Pattern MOVE_TO_ENTITY = Pattern.compile("PathNavigation\\.m_5624_:\\(Lnet/minecraft/world/entity/Entity;D\\)Z");
    private static final Pattern MOVE_TO_COORDS = Pattern.compile("PathNavigation\\.m_26519_:\\(DDDD\\)Z");
    private static final Pattern CIRCLE_ENTITY = Pattern.compile("ADragonBaseFlyingRideable\\.circleEntity:\\(Lnet/minecraft/world/phys/Vec3;FFZIFF\\)V");
    private static final Pattern BIPUSH = Pattern.compile("bipush\\s+(\\d+)");

    private GameplayBytecodeFixture() {}

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("usage: GameplayBytecodeFixture <original-isleofberk.jar>");
        }
        Path jar = Path.of(args[0]);
        if (!Files.isRegularFile(jar)) {
            throw new IOException("original dependency jar missing: " + jar);
        }

        String attackTick = methodBody(javap(jar, "com.GACMD.isleofberk.entity.AI.flight.own.DragonFlyAndAttackAirbourneTargetGoal"), "m_8037_();");
        require(count(MOVE_TO_ENTITY, attackTick) == 1, "DragonFlyAndAttackAirbourneTargetGoal.tick moveTo(Entity,D) count\n" + attackTick);
        require(count(MOVE_TO_COORDS, attackTick) == 0, "DragonFlyAndAttackAirbourneTargetGoal.tick must not call moveTo(DDDD)");

        String followTick = methodBody(javap(jar, "com.GACMD.isleofberk.entity.AI.flight.player.DragonFollowPlayerFlying"), "m_8037_();");
        require(count(MOVE_TO_COORDS, followTick) == 5, "DragonFollowPlayerFlying.tick moveTo(DDDD) count\n" + followTick);
        require(count(MOVE_TO_ENTITY, followTick) == 0, "DragonFollowPlayerFlying.tick must not call moveTo(Entity,D)");

        String circleTick = methodBody(javap(jar, "com.GACMD.isleofberk.entity.AI.flight.own.UntamedDragonCircleFlightGoal"), "m_8037_();");
        require(count(CIRCLE_ENTITY, circleTick) == 2, "UntamedDragonCircleFlightGoal.tick circleEntity count\n" + circleTick);

        String eggTick = methodBody(javap(jar, "com.GACMD.isleofberk.entity.eggs.entity.base.ADragonEggBase"), "m_8119_();");
        require(countBipush(eggTick, 20) == 1, "ADragonEggBase.tick bipush 20 count\n" + eggTick);
        require(countBipush(eggTick, 8) == 0, "ADragonEggBase.tick must not contain bipush 8");

        String shock = methodBody(javap(jar, "com.GACMD.isleofberk.effects.ShockEffect"), "m_6742_(net.minecraft.world.entity.LivingEntity, int);");
        require(countBipush(shock, 20) == 1, "ShockEffect.applyEffectTick bipush 20 (damage) count\n" + shock);
        require(countBipush(shock, 8) == 1, "ShockEffect.applyEffectTick bipush 8 (particle) count\n" + shock);

        System.out.println("GameplayBytecodeFixture: PASS (moveTo 1/5, circleEntity 2, egg 20x1, shock 20x1+8x1)");
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

    private static int count(Pattern pattern, String body) {
        int matches = 0;
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            matches++;
        }
        return matches;
    }

    private static int countBipush(String body, int value) {
        int matches = 0;
        Matcher matcher = BIPUSH.matcher(body);
        while (matcher.find()) {
            if (Integer.parseInt(matcher.group(1)) == value) {
                matches++;
            }
        }
        return matches;
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
