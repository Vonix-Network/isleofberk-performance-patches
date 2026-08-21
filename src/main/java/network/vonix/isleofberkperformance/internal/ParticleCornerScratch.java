package network.vonix.isleofberkperformance.internal;

import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;

/**
 * Per-instance particle-render scratch. {@code VertexConsumer.vertex} copies primitive
 * coordinates, so callees do not retain the {@link Vector3f} references.
 *
 * <p>Unexpected extra corner allocations fall back to a fresh {@code Vector3f} rather than
 * aliasing beyond the four-slot buffer.
 */
public final class ParticleCornerScratch {
    public static final int CORNER_COUNT = 4;

    private final Vector3f[] corners = {
            new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()
    };
    private int index;
    private Vec3 cameraPos;

    public void begin() {
        this.index = 0;
        this.cameraPos = null;
    }

    public Vector3f nextCorner(float x, float y, float z) {
        if (this.index >= CORNER_COUNT) {
            return new Vector3f(x, y, z);
        }
        Vector3f corner = this.corners[this.index++];
        corner.set(x, y, z);
        return corner;
    }

    public Vec3 cameraPosition(Camera camera) {
        Vec3 cached = this.cameraPos;
        if (cached == null) {
            cached = camera.getPosition();
            this.cameraPos = cached;
        }
        return cached;
    }
}
