package net.jp.hellparadise.betterrespawn.mixin.early;

import net.jp.hellparadise.betterrespawn.interfaces.IBetterRespawn;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketClientStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CPacketClientStatus.class)
public abstract class BR_CPacketClientStatus implements IBetterRespawn {

    private boolean betterRespawn$isBetterRespawn;

    /**
     * Reads the raw packet data from the data stream.
     */
    @Inject(method = "readPacketData", at = @At(value = "RETURN"))
    private void betterRespawn$readPacketData(PacketBuffer buf, CallbackInfo ci) {
        this.betterRespawn$isBetterRespawn = buf.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    @Inject(method = "writePacketData", at = @At(value = "RETURN"))
    private void betterRespawn$writePacketData(PacketBuffer buf, CallbackInfo ci) {
        buf.writeBoolean(this.betterRespawn$isBetterRespawn);
    }

    @Override
    public boolean betterRespawn$isBetterRespawn() {
        return this.betterRespawn$isBetterRespawn;
    }

    @Override
    public CPacketClientStatus betterRespawn$setBetterRespawn(boolean value) {
        this.betterRespawn$isBetterRespawn = value;
        return (CPacketClientStatus) (Object) this;
    }

}
