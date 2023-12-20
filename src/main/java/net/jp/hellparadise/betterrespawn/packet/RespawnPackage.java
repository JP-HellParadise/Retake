package net.jp.hellparadise.betterrespawn.packet;

import net.minecraft.network.play.client.CPacketClientStatus;

public class RespawnPackage extends CPacketClientStatus {
    public RespawnPackage() {
        super(CPacketClientStatus.State.PERFORM_RESPAWN);
    }
}
