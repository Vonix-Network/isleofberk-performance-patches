package network.vonix.isleofberkperformance.verification;

import java.nio.charset.StandardCharsets;
import java.util.List;

import network.vonix.isleofberkperformance.internal.PerformanceSettings;

/**
 * Focused SOL-002/003 checks for atomic AI snapshots and egg/shock primitive reloads.
 * Does not construct Minecraft or Forge runtime objects.
 */
public final class PerformanceSettingsSnapshotFixture {
    private PerformanceSettingsSnapshotFixture() {}

    public static void main(String[] args) throws Exception {
        // Defaults.
        PerformanceSettings.overwrite(true, 4, 20, 8);
        long defaults = PerformanceSettings.aiMoveSnapshot();
        require(PerformanceSettings.aiMoveThrottlingEnabled(defaults), "default AI enabled");
        require(PerformanceSettings.aiMoveIntervalTicks(defaults) == 4, "default AI interval");
        require(PerformanceSettings.eggHatchCheckIntervalTicks() == 20, "default egg interval");
        require(PerformanceSettings.shockParticleIntervalTicks() == 8, "default shock interval");
        require(PerformanceSettings.aiMoveThrottlingEnabled() == PerformanceSettings.aiMoveThrottlingEnabled(defaults),
                "enabled accessor matches packed snapshot");
        require(PerformanceSettings.aiMoveIntervalTicks() == PerformanceSettings.aiMoveIntervalTicks(defaults),
                "interval accessor matches packed snapshot");

        // Reload all four keys; one packed AI read must stay coherent with accessors.
        PerformanceSettings.overwrite(false, 6, 15, 12);
        long reloaded = PerformanceSettings.aiMoveSnapshot();
        require(!PerformanceSettings.aiMoveThrottlingEnabled(reloaded), "reload AI enabled=false in snapshot");
        require(PerformanceSettings.aiMoveIntervalTicks(reloaded) == 6, "reload AI interval=6 in snapshot");
        require(!PerformanceSettings.aiMoveThrottlingEnabled(), "reload AI enabled=false accessor");
        require(PerformanceSettings.aiMoveIntervalTicks() == 6, "reload AI interval=6 accessor");
        require(PerformanceSettings.eggHatchCheckIntervalTicks() == 15, "reload egg=15");
        require(PerformanceSettings.shockParticleIntervalTicks() == 12, "reload shock=12");

        // Unrelated repeated publish with same values keeps observable snapshots stable.
        PerformanceSettings.overwrite(false, 6, 15, 12);
        require(!PerformanceSettings.aiMoveThrottlingEnabled(), "stable enabled after repeat overwrite");
        require(PerformanceSettings.aiMoveIntervalTicks() == 6, "stable interval after repeat overwrite");
        require(PerformanceSettings.eggHatchCheckIntervalTicks() == 15, "stable egg after repeat overwrite");
        require(PerformanceSettings.shockParticleIntervalTicks() == 12, "stable shock after repeat overwrite");

        // Clamp behavior remains coherent in the packed tuple.
        PerformanceSettings.overwrite(true, 0, 0, 0);
        long clamped = PerformanceSettings.aiMoveSnapshot();
        require(PerformanceSettings.aiMoveIntervalTicks(clamped) == 1, "AI interval clamps low to 1");
        require(PerformanceSettings.eggHatchCheckIntervalTicks() == 1, "egg clamps low to 1");
        require(PerformanceSettings.shockParticleIntervalTicks() == 1, "shock clamps low to 1");
        PerformanceSettings.overwrite(true, 999, 999, 999);
        long clampedHigh = PerformanceSettings.aiMoveSnapshot();
        require(PerformanceSettings.aiMoveIntervalTicks(clampedHigh) == 20, "AI interval clamps high to 20");
        require(PerformanceSettings.eggHatchCheckIntervalTicks() == 200, "egg clamps high to 200");
        require(PerformanceSettings.shockParticleIntervalTicks() == 200, "shock clamps high to 200");

        // Restore defaults.
        PerformanceSettings.overwrite(true, 4, 20, 8);

        // Compiled egg/shock mixin bytecode must call PerformanceSettings primitives, not Forge config get.
        for (String className : List.of(
                "network.vonix.isleofberkperformance.mixin.ADragonEggBaseMixin",
                "network.vonix.isleofberkperformance.mixin.ShockEffectMixin",
                "network.vonix.isleofberkperformance.mixin.DragonFlyAndAttackAirbourneTargetGoalMixin",
                "network.vonix.isleofberkperformance.mixin.DragonFollowPlayerFlyingMixin",
                "network.vonix.isleofberkperformance.mixin.UntamedDragonCircleFlightGoalMixin"
        )) {
            byte[] bytes = readClassBytes(className);
            String latin1 = new String(bytes, StandardCharsets.ISO_8859_1);
            require(!latin1.contains("ForgeConfigSpec"), className + " must not reference ForgeConfigSpec");
            require(!latin1.contains("ConfigValue"), className + " must not reference ConfigValue");
            if (className.endsWith("ADragonEggBaseMixin")) {
                require(latin1.contains("eggHatchCheckIntervalTicks"), className + " must call eggHatchCheckIntervalTicks");
            }
            if (className.endsWith("ShockEffectMixin")) {
                require(latin1.contains("shockParticleIntervalTicks"), className + " must call shockParticleIntervalTicks");
            }
            if (className.endsWith("DragonFlyAndAttackAirbourneTargetGoalMixin")
                    || className.endsWith("DragonFollowPlayerFlyingMixin")
                    || className.endsWith("UntamedDragonCircleFlightGoalMixin")) {
                require(latin1.contains("aiMoveSnapshot"), className + " must read aiMoveSnapshot once-path");
            }
        }

        System.out.println("PerformanceSettingsSnapshotFixture: PASS (atomic AI tuple + egg/shock snapshot reload)");
    }

    private static byte[] readClassBytes(String className) throws Exception {
        String resource = className.replace('.', '/') + ".class";
        try (var in = PerformanceSettingsSnapshotFixture.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("missing class bytes: " + resource);
            }
            return in.readAllBytes();
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
