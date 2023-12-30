package net.jp.hellparadise.betterrespawn.mixin.early;

import net.jp.hellparadise.betterrespawn.interfaces.IBetterPlayerMP;
import net.jp.hellparadise.betterrespawn.packet.RespawnPackage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketClientStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayServer.class)
public abstract class BR_NetHandlerPlayServer {

    @Shadow
    public EntityPlayerMP player;

    @Inject(method = "processClientStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getPlayerList()Lnet/minecraft/server/management/PlayerList;", ordinal = 1))
    private void betterRespawn$processClientStatus(CPacketClientStatus packetIn, CallbackInfo ci) {
        if (packetIn instanceof RespawnPackage) {
            ((IBetterPlayerMP) this.player).betterRespawn$enableBetterRespawn();
        }
    }

}
