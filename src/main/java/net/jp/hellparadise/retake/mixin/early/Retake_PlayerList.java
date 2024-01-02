package net.jp.hellparadise.retake.mixin.early;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.jp.hellparadise.retake.components.RetakeDataManager;
import net.jp.hellparadise.retake.components.RetakeRandom;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public abstract class Retake_PlayerList {

    @WrapOperation(
        method = "recreatePlayerEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/WorldProvider;canRespawnHere()Z"
        )
    )
    private boolean betterRespawn$recreatePlayerEntity$canRespawnHere(WorldProvider instance, Operation<Boolean> original, EntityPlayerMP playerIn, @Share("newSpawn") LocalRef<BlockPos> newSpawn, @Share("isPlayerRetake") LocalBooleanRef isPlayerRetake) {
        isPlayerRetake.set(playerIn.getDataManager().get(RetakeDataManager.Retake$Init));
        if (isPlayerRetake.get()) {
            newSpawn.set(RetakeRandom.pickNewSpawnPos(playerIn.world, playerIn.getPosition()));
        }
        return newSpawn.get() != null || original.call(instance);
    }

    @Inject(
        method = "recreatePlayerEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/WorldServer;getChunkProvider()Lnet/minecraft/world/gen/ChunkProviderServer;"
        )
    )
    private void betterRespawn$recreatePlayerEntity$getBedLocation(EntityPlayerMP playerIn, int dimension, boolean conqueredEnd, CallbackInfoReturnable<EntityPlayerMP> cir, @Share("newSpawn") LocalRef<BlockPos> newSpawn, @Local(ordinal = 1) EntityPlayerMP entityplayermp) {
        if (newSpawn.get() != null) entityplayermp.setPosition(newSpawn.get().getX(), newSpawn.get().getY(), newSpawn.get().getZ());
    }

    @WrapOperation(
        method = "recreatePlayerEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/EntityPlayerMP;isSpawnForced(I)Z"
        )
    )
    private boolean betterRespawn$recreatePlayerEntity$isSpawnForced(EntityPlayerMP playerIn, int dimension, Operation<Boolean> original, @Share("isPlayerRetake") LocalBooleanRef isPlayerRetake) {
        return isPlayerRetake.get() || original.call(playerIn, dimension);
    }

}
