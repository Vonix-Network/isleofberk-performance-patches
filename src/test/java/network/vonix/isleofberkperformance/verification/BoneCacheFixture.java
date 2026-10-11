package network.vonix.isleofberkperformance.verification;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import software.bernie.geckolib3.core.processor.IBone;

import network.vonix.isleofberkperformance.internal.DragonBoneIndex;

/**
 * Deterministic contract fixture for the guarded GeckoLib bone index.
 * It tests duplicate-name first-match behavior, clear/re-register, misses, and
 * live-list mutation safety without launching Minecraft or mutating a runtime.
 */
public final class BoneCacheFixture {
    private BoneCacheFixture() {}

    public static void main(String[] args) throws Exception {
        verifyIndexBehavior();
        verifyMixinShape();
        verifySourceBoundary();
        System.out.println("BoneCacheFixture: PASS (IoB guard, exact GeckoLib hooks, duplicate/mutation/clear behavior)");
    }

    private static void verifyIndexBehavior() {
        DragonBoneIndex index = new DragonBoneIndex();
        List<IBone> bones = new ArrayList<>();
        IBone prefix = bone("prefix");
        IBone firstHead = bone("head");
        IBone duplicateHead = bone("head");
        IBone root = bone("root");

        bones.add(prefix);
        bones.add(firstHead);
        index.observeRegistered(firstHead, bones);
        require(index.lookup("head", bones) == firstHead, "first registered duplicate must win");

        bones.add(duplicateHead);
        index.observeRegistered(duplicateHead, bones);
        require(index.lookup("head", bones) == firstHead, "valid first duplicate must remain authoritative");

        bones.remove(firstHead);
        require(index.lookup("head", bones) == null, "removed cached bone must fail closed");
        index.observe("head", duplicateHead, bones);
        require(index.lookup("head", bones) == duplicateHead, "fallback result must re-index after removal");

        require(index.lookup("root", bones) == null, "misses must remain a fallback, not a false hit");
        bones.add(root);
        index.observeRegistered(root, bones);
        require(index.lookup("root", bones) == root, "live-list append must become discoverable");

        IBone earlierHead = bone("head");
        bones.set(0, earlierHead);
        require(index.lookup("head", bones) == null,
                "earlier direct replacement must not bypass GeckoLib first-match semantics");
        bones.set(0, prefix);
        index.observe("head", duplicateHead, bones);
        require(index.lookup("head", bones) == duplicateHead,
                "cache must recover after earlier duplicate is removed");

        index.clear();
        bones.clear();
        bones.add(duplicateHead);
        index.observeRegistered(duplicateHead, bones);
        bones.add(root);
        index.observeRegistered(root, bones);
        require(index.lookup("head", bones) == duplicateHead, "re-register after clear must work");
    }

    private static void verifyMixinShape() throws Exception {
        require(Class.forName(
                "network.vonix.isleofberkperformance.mixin.AnimationProcessorBoneCacheMixin") != null,
                "bone cache Mixin class must load");
        require(Class.forName(
                "software.bernie.geckolib3.core.processor.AnimationProcessor") != null,
                "pinned GeckoLib AnimationProcessor must load");
    }

    private static void verifySourceBoundary() throws Exception {
        Path source = Path.of("src/main/java/network/vonix/isleofberkperformance/mixin/AnimationProcessorBoneCacheMixin.java");
        String text = Files.readString(source);
        for (String required : List.of(
                "instanceof BaseDragonModel",
                "@Mixin(value = AnimationProcessor.class, remap = false)",
                "@At(\"HEAD\")",
                "cancellable = true",
                "require = 1",
                "clearModelRendererList()V",
                "registerModelRenderer(Lsoftware/bernie/geckolib3/core/processor/IBone;)V",
                "getBone(Ljava/lang/String;)Lsoftware/bernie/geckolib3/core/processor/IBone;",
                "DragonBoneIndex",
                "modelRendererList"
        )) {
            require(text.contains(required), "source boundary missing: " + required);
        }
        String json = Files.readString(Path.of("src/main/resources/isleofberkperformance.mixins.json"));
        require(json.contains("\"AnimationProcessorBoneCacheMixin\""),
                "client mixin registration missing");
        require(!json.contains("DragonCamera"), "camera behavior must remain excluded");
    }

    private static IBone bone(String name) {
        InvocationHandler handler = new BoneHandler(name);
        return (IBone) Proxy.newProxyInstance(
                IBone.class.getClassLoader(), new Class<?>[]{IBone.class}, handler);
    }

    private static final class BoneHandler implements InvocationHandler {
        private final String name;

        private BoneHandler(String name) {
            this.name = name;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "getName" -> name;
                case "toString" -> "Bone(" + name + ")";
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> defaultValue(method.getReturnType());
            };
        }

        private static Object defaultValue(Class<?> type) {
            if (!type.isPrimitive()) return null;
            if (type == boolean.class) return false;
            if (type == byte.class) return (byte) 0;
            if (type == short.class) return (short) 0;
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            if (type == float.class) return 0.0F;
            if (type == double.class) return 0.0D;
            if (type == char.class) return '\0';
            return null;
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
