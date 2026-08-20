package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.AI.flight.ADragonBaseBaseFlyingRideableGoal;
import com.GACMD.isleofberk.entity.base.dragon.ADragonBaseFlyingRideable;
import java.util.Map;
import java.util.UUID;
import network.vonix.isleofberkperformance.internal.TailingDragonLifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/** Exposes the pinned per-goal formation map only for its owning goal's stop lifecycle. */
@Mixin(value = ADragonBaseBaseFlyingRideableGoal.class, remap = false)
public abstract class ADragonBaseBaseFlyingRideableGoalMixin implements TailingDragonLifecycle {
    @Shadow(remap = false) protected Map<UUID, ADragonBaseFlyingRideable> tailingDragons;

    @Override
    @Unique
    public void vonix$clearTailingDragons() {
        this.tailingDragons.clear();
    }
}
