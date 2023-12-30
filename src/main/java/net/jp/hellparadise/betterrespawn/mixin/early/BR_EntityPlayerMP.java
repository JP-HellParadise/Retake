package net.jp.hellparadise.betterrespawn.mixin.early;

import net.jp.hellparadise.betterrespawn.interfaces.IBetterRespawn;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.client.CPacketClientStatus;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityPlayerMP.class)
public abstract class BR_EntityPlayerMP implements IBetterRespawn {

    private boolean betterRespawn$isBetterRespawn;

    @Override
    public boolean betterRespawn$isBetterRespawn() {
        return this.betterRespawn$isBetterRespawn;
    }

    @Override
    public CPacketClientStatus betterRespawn$setBetterRespawn(boolean value) {
        this.betterRespawn$isBetterRespawn = value;
        return null;
    }

}
