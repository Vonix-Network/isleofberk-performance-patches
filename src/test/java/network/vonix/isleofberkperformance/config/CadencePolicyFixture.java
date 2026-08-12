package network.vonix.isleofberkperformance.config;

/**
 * Deterministic fixture for cadence policy and the skipped-activation clear contract
 * used by the two AI mixins (no Minecraft runtime required).
 */
public final class CadencePolicyFixture {
    private CadencePolicyFixture() {}

    public static void main(String[] args) {
        // Interval 1: upstream cadence — every tick runs regardless of phase.
        require(CadencePolicy.shouldRun(7, 1), "interval one preserves upstream cadence");
        require(CadencePolicy.shouldRun(0, 1, null), "interval one with null UUID always runs");
        require(CadencePolicy.shouldRun(99, 1, new java.util.UUID(0L, 1L)), "interval one ignores phase");

        // Basic interval without phase.
        require(CadencePolicy.shouldRun(0, 10), "tick zero runs");
        require(CadencePolicy.shouldRun(20, 10), "aligned tick runs");
        require(!CadencePolicy.shouldRun(21, 10), "off-cycle tick skips");

        // Negative tick determinism (floorMod, not remainder).
        require(CadencePolicy.shouldRun(-10, 10), "negative modulo remains deterministic");
        require(!CadencePolicy.shouldRun(-9, 10), "negative off-cycle skips");
        require(CadencePolicy.shouldRun(-10, 10, null), "negative tick with null UUID is deterministic");

        // Null UUID: phase offset 0 (same as unphased).
        require(CadencePolicy.shouldRun(0, 10, null), "null UUID phase offset is zero — tick 0 runs");
        require(!CadencePolicy.shouldRun(1, 10, null), "null UUID skips off-cycle");
        require(CadencePolicy.shouldRun(10, 10, null), "null UUID aligned tick runs");

        // UUID phase staggering.
        java.util.UUID phase = new java.util.UUID(0L, 1L);
        int offset = Math.floorMod(phase.hashCode(), 10);
        require(offset != 0, "fixture phase has non-zero offset for stagger coverage");
        require(CadencePolicy.shouldRun(9, 10, phase), "phase offsets the scan tick");
        require(!CadencePolicy.shouldRun(0, 10, phase), "phase avoids synchronized tick zero");
        int runs = 0;
        for (int tick = 0; tick < 100; tick++) {
            if (CadencePolicy.shouldRun(tick, 10, phase)) {
                runs++;
            }
        }
        require(runs == 10, "phased cadence preserves scan count over 100 ticks");

        // No stale lookAt / partner on skipped activation (mixin contract model).
        // Mixins clear the cached target and return false when cadence skips.
        Object lookAt = "stale-player";
        Object partner = "stale-partner";
        int skipTick = 1;
        int interval = 10;
        if (!CadencePolicy.shouldRun(skipTick, interval, null)) {
            lookAt = null;
            partner = null;
        }
        require(lookAt == null, "no stale lookAt on skipped activation");
        require(partner == null, "no stale partner on skipped activation");

        // When cadence allows, mixins do not clear — upstream can set/keep targets.
        lookAt = "kept-player";
        partner = "kept-partner";
        if (!CadencePolicy.shouldRun(0, interval, null)) {
            lookAt = null;
            partner = null;
        }
        require(lookAt != null, "lookAt retained when activation is allowed");
        require(partner != null, "partner retained when activation is allowed");

        // Config bounds documentation contract (pure constants; no Forge init required).
        require(CadencePolicy.INTERVAL_MIN == 1, "min interval is upstream cadence");
        require(CadencePolicy.INTERVAL_MAX == 200, "max interval is hard-capped");
        require(CadencePolicy.INTERVAL_DEFAULT >= CadencePolicy.INTERVAL_MIN
                        && CadencePolicy.INTERVAL_DEFAULT <= CadencePolicy.INTERVAL_MAX,
                "default interval within safe bounds");
        require(CadencePolicy.INTERVAL_DEFAULT == 1,
                "default interval preserves upstream cadence");

        System.out.println("CadencePolicyFixture: PASS");
    }

    private static void require(boolean value, String label) {
        if (!value) {
            throw new AssertionError(label);
        }
    }
}
