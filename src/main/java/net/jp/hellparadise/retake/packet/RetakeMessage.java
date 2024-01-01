package net.jp.hellparadise.retake.packet;

import io.netty.buffer.ByteBuf;
import net.jp.hellparadise.retake.Retake;
import net.jp.hellparadise.retake.RetakeConfig;
import net.jp.hellparadise.retake.asm.transformer.GuiGameOverTransformer;
import net.jp.hellparadise.retake.components.RetakeDataManager;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RetakeMessage implements IMessage {

    private boolean requestCooldownInfo;
    private boolean cooldown;

    @Override
    public void fromBytes(ByteBuf buf) {
        this.requestCooldownInfo = buf.readBoolean();
        this.cooldown = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.requestCooldownInfo);
        buf.writeBoolean(this.cooldown);
    }

    public RetakeMessage isCooldown(boolean value) {
        this.cooldown = value;
        return this;
    }

    public RetakeMessage isRequestCooldownInfo() {
        this.requestCooldownInfo = true;
        return this;
    }

    public static class Handler implements IMessageHandler<RetakeMessage, IMessage> {

        @Override public IMessage onMessage(RetakeMessage message, MessageContext ctx) {
            if (ctx.side.isServer() && ctx.netHandler instanceof NetHandlerPlayServer netHandler) {
                if (message.requestCooldownInfo) {
                    Retake.proxy.getThreadListener(ctx).addScheduledTask(() -> Retake.NetworkHandler.INSTANCE.sendTo(
                        new RetakeMessage().isCooldown(RetakeDataManager.isCooldown(ctx.getServerHandler().player)), ctx.getServerHandler().player));
                } else if (netHandler.player.getHealth() <= 0.0F) {
                    netHandler.player = netHandler.player.server.getPlayerList().recreatePlayerEntity(netHandler.player, netHandler.player.dimension, false);
                    Retake.proxy.getThreadListener(ctx).addScheduledTask(() -> RetakeDataManager.setRetakeData(netHandler.player, RetakeConfig.cooldownAsTicks()));
                }
            } else if (ctx.side.isClient()) {
                GuiGameOverTransformer.Hook.isCooldown.set(message.cooldown);
            }

            return null;
        }

    }

}
