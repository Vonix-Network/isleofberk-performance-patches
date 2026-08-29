package network.vonix.isleofberkperformance.verification;

import java.util.List;

import network.vonix.isleofberkperformance.IsleOfBerkPerformanceMixinPlugin;
import network.vonix.isleofberkperformance.PassengerRenderGate;

/** Deterministic gates for the combined 1.3.4 Variant Loader passenger companion. */
public final class PassengerRenderCompanionFixture {
    private PassengerRenderCompanionFixture() {}

    public static void main(String[] args) {
        require(!PassengerRenderGate.shouldCancelEmptyPassengerRender(null), "null list must not cancel");
        require(PassengerRenderGate.shouldCancelEmptyPassengerRender(List.of()), "empty list must cancel");
        require(!PassengerRenderGate.shouldCancelEmptyPassengerRender(List.of(new Object())), "non-empty list must not cancel");

        String mixin = "network.vonix.isleofberkperformance.mixin.PassengerLayerMixin";
        require(IsleOfBerkPerformanceMixinPlugin.isPassengerLayerMixin(mixin), "passenger Mixin name must be recognized");
        require(IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                        mixin, true, IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.EXACT_2_7_0, true),
                "exact Variant Loader 2.7.0 client gate with valid fingerprint must apply");
        require(!IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                        mixin, true, IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.EXACT_2_7_0, false),
                "exact Variant Loader 2.7.0 client gate without fingerprint must fail closed");
        require(!IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                        mixin, false, IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.EXACT_2_7_0, true),
                "server gate must reject passenger Mixin");
        require(!IsleOfBerkPerformanceMixinPlugin.shouldApplyMixin(
                        mixin, true, IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.MISMATCHED, true),
                "mismatched Variant Loader version must fail closed");
        require(IsleOfBerkPerformanceMixinPlugin.classifyMetadata(
                        new IsleOfBerkPerformanceMixinPlugin.ModMetadata("iobvariantloader", "2.7.0"))
                        == IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.EXACT_2_7_0,
                "exact metadata must classify correctly");
        require(IsleOfBerkPerformanceMixinPlugin.classifyMetadata(
                        new IsleOfBerkPerformanceMixinPlugin.ModMetadata("iobvariantloader", "2.7.1"))
                        == IsleOfBerkPerformanceMixinPlugin.VariantLoaderMatch.MISMATCHED,
                "mismatched metadata must classify fail-closed");

        System.out.println("PassengerRenderCompanionFixture: PASS (empty/non-empty/null and exact client gate)");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
