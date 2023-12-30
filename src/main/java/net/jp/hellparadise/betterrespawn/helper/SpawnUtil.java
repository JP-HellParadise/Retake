package net.jp.hellparadise.betterrespawn.helper;

import java.util.Random;
import javax.annotation.Nullable;
import net.jp.hellparadise.betterrespawn.BetterRespawnConfig;
import net.jp.hellparadise.betterrespawn.BetterRespawnMod;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;

public class SpawnUtil {

    @Nullable public static BlockPos findValidRespawnLocation(EntityPlayerMP playerIn) {

        int min = BetterRespawnConfig.instance().minRespawnRadius;
        int max = BetterRespawnConfig.instance().maxRespawnRadius;
        int retry = BetterRespawnConfig.instance().retryAttempt;

        BlockPos deathLocation = playerIn.getPosition();
        WorldServer world = playerIn.getServerWorld();

        BlockPos pos = null;
        BetterRespawnMod.LOGGER.debug("Searching for respawn location for player {} (UUID: {})", playerIn.getName(), playerIn.getUniqueID());
        for (int i = 0; i < retry && pos == null; i++) {
            BetterRespawnMod.LOGGER.debug("Attempt {}/{}", i + 1, retry);
            pos = world.getTopSolidOrLiquidBlock(new BlockPos(
                    SpawnUtil.getRandomRange(deathLocation.getX(), min, max, world.rand),
                    0,
                    SpawnUtil.getRandomRange(deathLocation.getZ(), min, max, world.rand)
                ));
            if (!WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, world, pos)) {
                pos = null;
            }
        }
        if (pos == null) {
            BetterRespawnMod.LOGGER.debug("Found no valid respawn location after {} attempts", retry);
            BetterRespawnMod.LOGGER.debug("Use minecraft respawn point instead");
        } else {
            BetterRespawnMod.LOGGER.debug("Found valid respawn location: [{}, {}, {}]", pos.getX(), pos.getY(), pos.getZ());
        }
        return pos;
    }

    private static int getRandomRange(int actual, int minDistance, int maxDistance, Random rand) {
        return actual + (rand.nextBoolean() ? -1 : 1) * (minDistance + rand.nextInt(maxDistance - minDistance));
    }

}
