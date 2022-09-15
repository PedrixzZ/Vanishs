package me.drex.vanish.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.vanish.api.VanishAPI;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @Shadow
    @Nullable
    public abstract Entity getEntity(int i);

    @WrapOperation(
            method = "destroyBlockProgress",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    public void vanish_hideBlockDestroyProgress(ServerGamePacketListenerImpl packetListener, Packet<?> packet, Operation<Void> original) {
        Entity entity = this.getEntity(((ClientboundBlockDestructionPacket) packet).getId());
        if (!(entity instanceof ServerPlayer player) || VanishAPI.canSeePlayer(player, packetListener.player)) {
            original.call(packetListener, packet);
        }
    }

}
