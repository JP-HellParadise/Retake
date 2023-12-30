package net.jp.hellparadise.betterrespawn.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.jp.hellparadise.betterrespawn.helper.SpawnUtil;
import net.jp.hellparadise.betterrespawn.interfaces.IBetterPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public abstract class BR_PlayerList {

    @WrapOperation(
        method = "recreatePlayerEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/WorldProvider;canRespawnHere()Z"
        )
    )
    private boolean betterRespawn$recreatePlayerEntity$canRespawnHere(WorldProvider instance, Operation<Boolean> original, EntityPlayerMP playerIn, @Share("newSpawn") LocalRef<BlockPos> newSpawn) {
        newSpawn.set(SpawnUtil.findValidRespawnLocation(playerIn.getServerWorld(), playerIn.getPosition()));
        return ((IBetterPlayerMP) playerIn).betterRespawn$isBetterRespawn() && newSpawn.get() != null
            || original.call(instance);
    }


    @Inject(
        method = "recreatePlayerEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/WorldServer;getChunkProvider()Lnet/minecraft/world/gen/ChunkProviderServer;"
        )
    )
    private void betterRespawn$recreatePlayerEntity$getBedLocation(EntityPlayerMP playerIn, int dimension, boolean conqueredEnd, CallbackInfoReturnable<EntityPlayerMP> cir, @Share("newSpawn") LocalRef<BlockPos> newSpawn, @Local(ordinal = 1) EntityPlayerMP entityplayermp) {
        if (((IBetterPlayerMP) playerIn).betterRespawn$isBetterRespawn()) {
            if (newSpawn.get() != null) entityplayermp.setPosition(newSpawn.get().getX(), newSpawn.get().getY(), newSpawn.get().getZ());
        }
    }

    @WrapOperation(
        method = "recreatePlayerEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/EntityPlayerMP;isSpawnForced(I)Z"
        )
    )
    private boolean betterRespawn$recreatePlayerEntity$isSpawnForced(EntityPlayerMP playerIn, int dimension, Operation<Boolean> original) {
        return ((IBetterPlayerMP) playerIn).betterRespawn$isBetterRespawn() || original.call(playerIn, dimension);
    }

}
