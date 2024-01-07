package net.jp.hellparadise.retake.components;

import javax.annotation.Nullable;
import net.jp.hellparadise.retake.RetakeConfig;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import org.apache.commons.lang3.RandomUtils;

/**
 * From <a href="https://github.com/Nomifactory/DimensionalEdibles/blob/master/src/main/java/jackyy/dimensionaledibles/util/TeleporterHandler.java#L156">DimensionalEdibles</a>
 */
public class RetakeRandom {

    @Nullable public static BlockPos pickNewSpawnPos(World world, BlockPos originalPos) {
        BlockPos newPos = null;

        for (int i = 0; newPos == null || i < RetakeConfig.global.retryAttempt ; i++) {
            int x = originalPos.getX() + (world.rand.nextBoolean() ? -1 : 1) * RandomUtils.nextInt(RetakeConfig.global.minRadius, RetakeConfig.global.maxRadius);
            int z = originalPos.getZ() + (world.rand.nextBoolean() ? 1 : -1) * RandomUtils.nextInt(RetakeConfig.global.minRadius, RetakeConfig.global.maxRadius);

            newPos = getValidYSpawnPos(world, new BlockPos(x, originalPos.getY(), z));

            if (!WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, world, newPos))
            {
                newPos = null;
            }
        }

        return newPos;
    }

    public static BlockPos getValidYSpawnPos(World world, BlockPos basePos) {
        MutableBlockPos pos = new MutableBlockPos(basePos.getX(), basePos.getY(), basePos.getZ());
        MutableBlockPos spawnPosition = new MutableBlockPos(-1, -1, -1);
        int possibleYPosition = 0;

        do {
            //Scan the x,z coordinate point along the Y column to find a possible spawn location. Returns -1 if no location is found
            possibleYPosition = scanColumn(world, pos.getX(), pos.getZ(), pos.getY());
            if(possibleYPosition == -1) {
                incrementColumn(pos, basePos);
            }
            else {
                spawnPosition.setPos(pos.getX(), possibleYPosition, pos.getZ());
            }
        }
        while(spawnPosition.getY() == -1);

        return spawnPosition;
    }

    public static int scanColumn(World world, int x, int z, int targetY) {
        int possibleY = -1;
        //start searching Y locations at 4 to avoid vanilla bedrock generation
        for(int currentY = 4; currentY < world.getActualHeight(); currentY++) {
            BlockPos pos = new BlockPos(x, currentY, z);
            boolean isBlockBelowSolid = world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP);
            boolean isLegBlockFree = world.getBlockState(pos).getBlock().isAir(world.getBlockState(pos), world, pos);
            boolean isChestBlockFree = world.getBlockState(pos.up()).getBlock().isAir(world.getBlockState(pos.up()), world, pos);

            //Check to see if the block below the player's feet is solid, and if the player has a two block spawning area
            if(isBlockBelowSolid && isChestBlockFree && isLegBlockFree) {
                //The first instance of a possible spawning location found
                if(possibleY == -1) {
                    possibleY = currentY;
                }
                else {
                    //If the currentY is closer to the targetY than the current possibleY,
                    //set that as the new possible spawning location
                    if(Math.abs(possibleY - targetY) > Math.abs(pos.getY() - targetY)) {
                        possibleY = currentY;
                    }
                }
            }

        }

        return possibleY;
    }

    public static void incrementColumn(MutableBlockPos currentPos, BlockPos originalPos) {
        double tempPosIncrementX = originalPos.getDistance(currentPos.getX() + 1, currentPos.getY(), currentPos.getZ());
        double tempPosIncrementZ = originalPos.getDistance(currentPos.getX(), currentPos.getY(), currentPos.getZ() + 1);

        if(tempPosIncrementX > tempPosIncrementZ) {
            currentPos.setPos(currentPos.getX(), currentPos.getY(), currentPos.getZ() + 1);
        }
        else {
            currentPos.setPos(currentPos.getX() + 1, currentPos.getY(), currentPos.getZ());
        }
    }

}
