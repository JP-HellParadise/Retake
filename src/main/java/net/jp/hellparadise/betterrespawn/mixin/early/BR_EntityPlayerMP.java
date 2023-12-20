package net.jp.hellparadise.betterrespawn.mixin.early;

import net.jp.hellparadise.betterrespawn.interfaces.IBetterPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityPlayerMP.class)
public abstract class BR_EntityPlayerMP implements IBetterPlayerMP {

    private boolean betterRespawn$isBetterRespawn = false;

    @Override
    public boolean betterRespawn$isBetterRespawn() {
        return betterRespawn$isBetterRespawn;
    }

    @Override
    public void betterRespawn$enableBetterRespawn() {
        betterRespawn$isBetterRespawn = true;
    }
}
