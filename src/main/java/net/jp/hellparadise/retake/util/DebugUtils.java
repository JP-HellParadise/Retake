package net.jp.hellparadise.retake.util;

import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.jp.hellparadise.retake.Retake;
import net.jp.hellparadise.retake.RetakeConfig;

public class DebugUtils {

    private static WeakHashMap<UUID, AtomicLong> debugList;

    public static void startDebug(UUID uuid) {
        if (RetakeConfig.debugCounter && debugList == null) {
            debugList = new WeakHashMap<>();
        }

        Retake.LOGGER.info("Start cooldown on UUID {}", uuid);
        debugList.putIfAbsent(uuid, new AtomicLong(System.currentTimeMillis()));
    }

    public static void stopDebug(UUID uuid) {
        if (debugList == null || debugList.isEmpty()) {
            return;
        }

        debugList.computeIfPresent(uuid, (key, val) -> {
            Retake.LOGGER.info("UUID: {}, completed cooldown after {}s", key.toString(),TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - val.get()));
            return null;
        });
    }

}
