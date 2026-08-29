package network.vonix.isleofberkperformance;

import java.util.List;

/**
 * Semantic boundary for the optional Variant Loader passenger-render Mixin.
 * Cancel is allowed only for a non-null empty passenger list.
 */
public final class PassengerRenderGate {
    private PassengerRenderGate() {}

    /**
     * @return true only when passengers is non-null and empty
     */
    public static boolean shouldCancelEmptyPassengerRender(List<?> passengers) {
        return passengers != null && passengers.isEmpty();
    }
}
