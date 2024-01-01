package net.jp.hellparadise.retake;

import net.jp.hellparadise.retake.packet.RetakeMessage;
import net.jp.hellparadise.retake.proxy.RetakeCommon;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class Retake {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @SidedProxy(clientSide = "net.jp.hellparadise.retake.proxy.RetakeClient", serverSide = "net.jp.hellparadise.retake.proxy.RetakeCommon")
    public static RetakeCommon proxy;

    /**
     * <a href="https://wiki.cleanroommc.com/mod-development/event/overview/">
     *     Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
     * </a>
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);

        LOGGER.info("Initialize network channel {}!", Tags.MOD_NAME);
        NetworkHandler.initialNetwork();
    }

    public static class NetworkHandler {
        private NetworkHandler() {}

        public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);

        private static void initialNetwork() {
            int id = 0;

            // Register packet
            INSTANCE.registerMessage(RetakeMessage.Handler.class, RetakeMessage.class, id, Side.CLIENT);
            INSTANCE.registerMessage(RetakeMessage.Handler.class, RetakeMessage.class, id++, Side.SERVER);
        }
    }

}
