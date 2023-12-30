package net.jp.hellparadise.betterrespawn.helper;

import java.util.Random;
import javax.annotation.Nullable;
import net.jp.hellparadise.betterrespawn.BetterRespawnConfig;
import net.jp.hellparadise.betterrespawn.BetterRespawnMod;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldServer;

public class SpawnUtil {

    private final Random random;

    public SpawnUtil() {
        random = new Random();
    }

    public static SpawnUtil instance;

    @Nullable public static BlockPos findValidRespawnLocation(WorldServer world, BlockPos deathLocation) {
        if (instance == null) {
            instance = new SpawnUtil();
        }

        int min = BetterRespawnConfig.instance().minRespawnRadius;
        int max = BetterRespawnConfig.instance().maxRespawnRadius;
        int retry = BetterRespawnConfig.instance().retryAttempt;

        BlockPos pos = null;
        for (int i = 0; i < retry && pos == null; i++) {
            BetterRespawnMod.LOGGER.debug("Searching for respawn location - Attempt {}/{}", i + 1, retry);
            pos = world.getTopSolidOrLiquidBlock(new BlockPos(
                    instance.getRandomRange(deathLocation.getX(), min, max),
                    0,
                    instance.getRandomRange(deathLocation.getZ(), min, max)
                ));
            if (!WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, world, pos)) {
                pos = null;
            }
        }
        if (pos == null) {
            BetterRespawnMod.LOGGER.info("Found no valid respawn location after {} attempts", retry);
            BetterRespawnMod.LOGGER.info("Use minecraft fallback as respawn location");
        } else {
            BetterRespawnMod.LOGGER.info("Found valid respawn location: [{}, {}, {}]", pos.getX(), pos.getY(), pos.getZ());
        }
        return pos;
    }

    private int getRandomRange(int actual, int minDistance, int maxDistance) {
        return actual + (random.nextBoolean() ? -1 : 1) * (minDistance + random.nextInt(maxDistance - minDistance));
    }

}
