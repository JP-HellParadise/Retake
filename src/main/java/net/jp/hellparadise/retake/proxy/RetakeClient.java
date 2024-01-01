package net.jp.hellparadise.retake.proxy;

import javax.annotation.Nonnull;
import net.jp.hellparadise.retake.Retake;
import net.jp.hellparadise.retake.Tags;
import net.jp.hellparadise.retake.packet.RetakeMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = Tags.MOD_ID)
@SuppressWarnings("unused")
public class RetakeClient extends RetakeCommon {

    @Nonnull
    @Override
    public IThreadListener getThreadListener(MessageContext context) {
        if (context.side.isClient()) {
            return Minecraft.getMinecraft();
        } else {
            return context.getServerHandler().player.server;
        }
    }

    @SubscribeEvent
    public static void onGuiOpenEvent(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiGameOver) {
            // Check if we are on cooldown or not
            Retake.NetworkHandler.INSTANCE.sendToServer(new RetakeMessage().isRequestCooldownInfo());
        }
    }

}
