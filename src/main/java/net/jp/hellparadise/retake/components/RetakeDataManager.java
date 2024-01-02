package net.jp.hellparadise.retake.components;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import java.util.UUID;
import net.jp.hellparadise.retake.Tags;
import net.jp.hellparadise.retake.util.DebugUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class RetakeDataManager {

    public static final DataParameter<Boolean> Retake$Init = EntityDataManager.createKey(EntityPlayer.class, DataSerializers.BOOLEAN);

    @SubscribeEvent
    public static void onPlayerTickEvent(EntityEvent.EntityConstructing event) {
        if (event.getEntity() instanceof EntityPlayer player) {
            player.getDataManager().register(Retake$Init, false);
        }
    }

    private final Object2IntArrayMap<UUID> playerList;

    @SubscribeEvent
    public void onPlayerConnectEvent(PlayerEvent.PlayerLoggedInEvent event) {
        RetakeDataManager.instance().playerList.putIfAbsent(event.player.getUniqueID(), 0);
    }

    @SubscribeEvent
    public static void onServerTickEvent(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            RetakeDataManager.instance().decrement();
        }
    }

    public RetakeDataManager() {
        playerList = new Object2IntArrayMap<>();
    }

    private static RetakeDataManager instance;

    public static RetakeDataManager instance() {
        if (instance == null) {
            instance = new RetakeDataManager();
        }

        return instance;
    }

    public static int getRetakeData(EntityPlayer player) {
        return RetakeDataManager.instance().playerList.getInt(player.getUniqueID());
    }

    public static void setRetakeData(EntityPlayer player, int value) {
        RetakeDataManager.instance().playerList.put(player.getUniqueID(), value);
        DebugUtils.startDebug(player.getUniqueID());
    }

    private void decrement() {
        for (UUID uuid : playerList.keySet()) {
            playerList.computeIfPresent(uuid, (key, val) -> {
                if (val > 0) return --val;
                DebugUtils.stopDebug(uuid);
                return val;
            });
        }
    }

    public static boolean isCooldown(EntityPlayer player) {
        return getRetakeData(player) > 0;
    }

}
