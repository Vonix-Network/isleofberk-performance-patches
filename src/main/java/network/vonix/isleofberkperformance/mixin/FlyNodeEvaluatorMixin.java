package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.entity.base.path.FlyNodeEvaluator;
import java.util.EnumMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.pathfinder.Node;
import network.vonix.isleofberkperformance.internal.EnumMapScratch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Reuses the per-call neighbor {@link EnumMap} and skips the redundant second
 * {@code MutableBlockPos.set} in {@code getBlockPathType}. {@code getNeighbors} is not overwritten.
 */
@Mixin(value = FlyNodeEvaluator.class, remap = true)
public abstract class FlyNodeEvaluatorMixin {
    @Unique
    private final EnumMapScratch<Direction, Node> vonix$dirNodes = new EnumMapScratch<>(Direction.class);

    @Inject(
            method = "getNeighbors([Lnet/minecraft/world/level/pathfinder/Node;Lnet/minecraft/world/level/pathfinder/Node;)I",
            at = @At("HEAD"),
            require = 1
    )
    private void vonix$beginNeighbors(Node[] neighbors, Node node, CallbackInfoReturnable<Integer> cir) {
        this.vonix$dirNodes.release();
    }

    @Inject(
            method = "getNeighbors([Lnet/minecraft/world/level/pathfinder/Node;Lnet/minecraft/world/level/pathfinder/Node;)I",
            at = @At("RETURN"),
            require = 1
    )
    private void vonix$endNeighbors(Node[] neighbors, Node node, CallbackInfoReturnable<Integer> cir) {
        this.vonix$dirNodes.release();
    }

    @Redirect(
            method = "getNeighbors([Lnet/minecraft/world/level/pathfinder/Node;Lnet/minecraft/world/level/pathfinder/Node;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Maps;newEnumMap(Ljava/lang/Class;)Ljava/util/EnumMap;",
                    remap = false
            ),
            require = 1
    )
    private EnumMap<?, ?> vonix$reuseNeighborMap(Class<?> keyType) {
        return this.vonix$dirNodes.acquire(keyType);
    }

    @Redirect(
            method = "getBlockPathType(Lnet/minecraft/world/level/BlockGetter;IIILnet/minecraft/world/entity/Mob;IIIZZ)Lnet/minecraft/world/level/pathfinder/BlockPathTypes;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;set(III)Lnet/minecraft/core/BlockPos$MutableBlockPos;",
                    ordinal = 1
            ),
            require = 1
    )
    private BlockPos.MutableBlockPos vonix$skipRedundantSet(BlockPos.MutableBlockPos pos, int x, int y, int z) {
        return pos;
    }
}
