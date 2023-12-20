package net.jp.hellparadise.betterrespawn.mixin.early;

import net.jp.hellparadise.betterrespawn.interfaces.IBetterPlayerSP;
import net.jp.hellparadise.betterrespawn.packet.RespawnPackage;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityPlayerSP.class)
public abstract class BR_EntityPlayerSP implements IBetterPlayerSP {
    @Final
    @Shadow
    public NetHandlerPlayClient connection;

    public void betterRespawn$respawnPlayer()
    {
        this.connection.sendPacket(new RespawnPackage());
    }
}
