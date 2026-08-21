package network.vonix.isleofberkperformance.verification;

import net.minecraft.resources.ResourceLocation;
import network.vonix.isleofberkperformance.internal.RenderResourceCache;

/** Deterministic contract fixture for the bounded 1.2 render-resource cache. */
public final class RenderResourceCacheFixture {
    private RenderResourceCacheFixture() {}

    public static void main(String[] args) {
        ResourceLocation modelA = RenderResourceCache.twoArg(
                "isleofberk", "geo/dragons/gronckle.geo.json");
        ResourceLocation modelB = RenderResourceCache.twoArg(
                "isleofberk", "geo/dragons/gronckle.geo.json");
        require(modelA == modelB, "known two-argument model resource must be reused");

        ResourceLocation glowA = RenderResourceCache.oneArg(
                "isleofberk:textures/dragons/night_fury/night_fury_glow.png");
        ResourceLocation glowB = RenderResourceCache.oneArg(
                "isleofberk:textures/dragons/night_fury/night_fury_glow.png");
        require(glowA == glowB, "known one-argument glow resource must be reused");

        ResourceLocation otherNamespace = RenderResourceCache.twoArg(
                "othermod", "textures/example.png");
        require("othermod".equals(otherNamespace.getNamespace()),
                "non-Isle-of-Berk namespace must preserve fallback behavior");

        ResourceLocation dynamic = RenderResourceCache.twoArg(
                "isleofberk", "textures/dynamic/variant.png");
        require("textures/dynamic/variant.png".equals(dynamic.getPath()),
                "unknown Isle-of-Berk paths must preserve fallback behavior");

        System.out.println("RenderResourceCacheFixture: PASS (bounded known-path reuse and fallback preservation)");
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
