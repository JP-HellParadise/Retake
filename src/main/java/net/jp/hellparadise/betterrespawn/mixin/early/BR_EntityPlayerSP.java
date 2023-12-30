package net.jp.hellparadise.betterrespawn.mixin.early;

import net.jp.hellparadise.betterrespawn.interfaces.IBetterPlayerSP;
import net.jp.hellparadise.betterrespawn.interfaces.IBetterRespawn;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketClientStatus;
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
        this.connection.sendPacket(((IBetterRespawn) new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN)).betterRespawn$setBetterRespawn(true));
    }

}
