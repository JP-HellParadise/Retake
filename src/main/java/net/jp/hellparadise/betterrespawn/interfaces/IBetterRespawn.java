package net.jp.hellparadise.betterrespawn.interfaces;

import net.minecraft.network.play.client.CPacketClientStatus;

public interface IBetterRespawn {

    boolean betterRespawn$isBetterRespawn();
    CPacketClientStatus betterRespawn$setBetterRespawn(boolean value);

}
