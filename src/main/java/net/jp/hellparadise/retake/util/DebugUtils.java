package net.jp.hellparadise.retake.util;

import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.jp.hellparadise.retake.Retake;
import net.jp.hellparadise.retake.RetakeConfig;
import net.minecraft.entity.player.EntityPlayer;

public class DebugUtils {

    private static WeakHashMap<EntityPlayer, AtomicLong> debugList;

    public static void startDebug(EntityPlayer player) {
        if (RetakeConfig.debugCounter || debugList == null) {
            debugList = new WeakHashMap<>();
        }

        debugList.putIfAbsent(player, new AtomicLong(System.currentTimeMillis()));
    }

    public static void stopDebug(EntityPlayer player) {
        if (debugList == null || debugList.isEmpty()) {
            return;
        }

        debugList.computeIfPresent(player, (key, val) -> {
            Retake.LOGGER.info(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - val.get()));
            return null;
        });
    }

}
