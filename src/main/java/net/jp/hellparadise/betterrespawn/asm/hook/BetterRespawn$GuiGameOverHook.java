package net.jp.hellparadise.betterrespawn.asm.hook;

import net.jp.hellparadise.betterrespawn.BetterRespawnConfig;
import net.jp.hellparadise.betterrespawn.BetterRespawnMod;
import net.jp.hellparadise.betterrespawn.helper.BetterRespawnButton;
import net.jp.hellparadise.betterrespawn.interfaces.IBetterPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * GuiGameOver hook
 */
@SuppressWarnings("unused")
public interface BetterRespawn$GuiGameOverHook {

    default void BetterRespawn$hook$actionPerformed(GuiScreen guiScreen, GuiButton guiButton) {
        if (guiButton instanceof BetterRespawnButton) {
            ((IBetterPlayerSP) guiScreen.mc.player).betterRespawn$respawnPlayer();
            BetterRespawnMod.clientRespawnCooldown = BetterRespawnConfig.instance().respawnCooldown;
            guiScreen.mc.displayGuiScreen(null);
        }
    }

    void BetterRespawn$hook$drawScreen(int mouseX, int mouseY);

    default boolean BetterRespawn$hook$updateScreen(GuiButton guiButton) {
        return guiButton instanceof BetterRespawnButton && BetterRespawnMod.clientRespawnCooldown > 0;
    }

}
