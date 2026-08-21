package network.vonix.isleofberkperformance.mixin;

import com.GACMD.isleofberk.network.message.MessageShockParticle;
import com.GACMD.isleofberk.network.message.util.ClientPacketHandlerClass;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Reuses Minecraft, entity, and Skrill particle-option lookups inside the original
 * {@code handleSpawnShockParticles} loop. Control flow, {@code new Random()}, and
 * {@code nextFloat} consumption stay with the original method.
 */
@Mixin(value = ClientPacketHandlerClass.class, remap = false)
public abstract class ClientPacketHandlerClassMixin {
    @Unique
    private static Minecraft vonix$minecraft;
    @Unique
    private static Entity vonix$entity;
    @Unique
    private static boolean vonix$entityResolved;
    @Unique
    private static Object vonix$skrillParticle;

    @Inject(method = "handleSpawnShockParticles", at = @At("HEAD"), require = 1, remap = false)
    private static void vonix$beginShockParticles(MessageShockParticle msg, Supplier<NetworkEvent.Context> ctx, CallbackInfo ci) {
        vonix$clearShockLookups();
    }

    @Inject(method = "handleSpawnShockParticles", at = @At("RETURN"), require = 1, remap = false)
    private static void vonix$endShockParticles(MessageShockParticle msg, Supplier<NetworkEvent.Context> ctx, CallbackInfo ci) {
        vonix$clearShockLookups();
    }

    @Redirect(
            method = "handleSpawnShockParticles",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getInstance()Lnet/minecraft/client/Minecraft;", remap = true),
            require = 4,
            remap = false
    )
    private static Minecraft vonix$reuseMinecraft() {
        Minecraft cached = vonix$minecraft;
        if (cached == null) {
            cached = Minecraft.getInstance();
            vonix$minecraft = cached;
        }
        return cached;
    }

    @Redirect(
            method = "handleSpawnShockParticles",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getEntity(I)Lnet/minecraft/world/entity/Entity;", remap = true),
            require = 1,
            remap = false
    )
    private static Entity vonix$reuseEntity(ClientLevel level, int id) {
        if (!vonix$entityResolved) {
            vonix$entity = level.getEntity(id);
            vonix$entityResolved = true;
        }
        return vonix$entity;
    }

    @Redirect(
            method = "handleSpawnShockParticles",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/registries/RegistryObject;get()Ljava/lang/Object;", remap = false),
            require = 1,
            remap = false
    )
    private static Object vonix$reuseSkrillParticle(RegistryObject<?> registry) {
        Object cached = vonix$skrillParticle;
        if (cached == null) {
            cached = registry.get();
            vonix$skrillParticle = cached;
        }
        return cached;
    }

    @Unique
    private static void vonix$clearShockLookups() {
        vonix$minecraft = null;
        vonix$entity = null;
        vonix$entityResolved = false;
        vonix$skrillParticle = null;
    }
}
