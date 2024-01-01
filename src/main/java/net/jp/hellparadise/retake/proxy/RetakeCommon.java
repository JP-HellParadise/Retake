package net.jp.hellparadise.retake.proxy;

import javax.annotation.Nonnull;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RetakeCommon {

    @Nonnull
    public IThreadListener getThreadListener(MessageContext context) {
        if (context.side.isServer()) {
            return context.getServerHandler().player.server;
        } else {
            throw new RuntimeException(
                "Tried to get the IThreadListener from a client-side MessageContext on the dedicated server");
        }
    }

}
