package network.vonix.isleofberkperformance.verification;

import java.util.EnumMap;
import java.util.concurrent.TimeUnit;

import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;
import net.minecraft.world.level.pathfinder.Node;
import network.vonix.isleofberkperformance.internal.EnumMapScratch;
import network.vonix.isleofberkperformance.internal.ParticleCornerScratch;

/** Deterministic contracts for V1.3 scratch reuse and fallback/negative cases. */
public final class V13HotPathFixture {
    private V13HotPathFixture() {}

    public static void main(String[] args) {
        verifyParticleCorners();
        verifyEnumMapScratch();
        System.out.println("V13HotPathFixture: PASS (corner reuse/fallback, EnumMap reuse/reentrancy)");
    }

    private static void verifyParticleCorners() {
        ParticleCornerScratch scratch = new ParticleCornerScratch();
        scratch.begin();
        Vector3f a0 = scratch.nextCorner(-1.0F, -1.0F, 0.0F);
        Vector3f a1 = scratch.nextCorner(-1.0F, 1.0F, 0.0F);
        Vector3f a2 = scratch.nextCorner(1.0F, 1.0F, 0.0F);
        Vector3f a3 = scratch.nextCorner(1.0F, -1.0F, 0.0F);
        require(a0 != a1 && a1 != a2 && a2 != a3, "four corner slots must be distinct");
        require(a0.x() == -1.0F && a0.y() == -1.0F && a0.z() == 0.0F, "corner 0 must keep constructor args");
        require(a2.x() == 1.0F && a2.y() == 1.0F && a2.z() == 0.0F, "corner 2 must keep constructor args");

        Vector3f overflow = scratch.nextCorner(9.0F, 8.0F, 7.0F);
        require(overflow != a0 && overflow != a1 && overflow != a2 && overflow != a3,
                "unexpected extra allocation must fall back to a fresh Vector3f");
        require(overflow.x() == 9.0F && overflow.y() == 8.0F && overflow.z() == 7.0F,
                "fallback Vector3f must keep constructor args");

        scratch.begin();
        Vector3f b0 = scratch.nextCorner(-1.0F, -1.0F, 0.0F);
        Vector3f b1 = scratch.nextCorner(-1.0F, 1.0F, 0.0F);
        Vector3f b2 = scratch.nextCorner(1.0F, 1.0F, 0.0F);
        Vector3f b3 = scratch.nextCorner(1.0F, -1.0F, 0.0F);
        require(b0 == a0 && b1 == a1 && b2 == a2 && b3 == a3,
                "begin() must reuse the same four per-instance corners");
        require(b0.x() == -1.0F && b3.y() == -1.0F, "reused corners must be reset from constructor args");
    }

    private static void verifyEnumMapScratch() {
        EnumMapScratch<Direction, Node> scratch = new EnumMapScratch<>(Direction.class);
        EnumMap<Direction, Node> first = scratch.acquire(Direction.class);
        require(first != null, "acquire must return a map");
        first.put(Direction.UP, null);
        EnumMap<Direction, Node> nested = scratch.acquire(Direction.class);
        require(nested != first, "re-entrant acquire must allocate a fresh EnumMap");
        scratch.release();

        EnumMap<Direction, Node> second = scratch.acquire(Direction.class);
        require(second == first, "release then acquire must reuse the scratch EnumMap");
        require(second.isEmpty(), "release must drop retained entries");
        scratch.release();

        EnumMap<?, ?> other = scratch.acquire(TimeUnit.class);
        require(other != first, "mismatched key type must not alias the Direction scratch");
        scratch.release();
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
