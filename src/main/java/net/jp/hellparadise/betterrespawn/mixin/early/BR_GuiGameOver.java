package net.jp.hellparadise.betterrespawn.mixin.early;

import net.jp.hellparadise.betterrespawn.interfaces.IBetterPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGameOver.class)
public abstract class BR_GuiGameOver extends GuiScreen {

    @Inject(
            method = "initGui",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 3, shift = At.Shift.AFTER)
    )
    private void betterRespawn$initGui(CallbackInfo ci) {
        for (GuiButton guibutton : this.buttonList)
        {
            guibutton.y += 12;
        }
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 60, I18n.format("Respawn Nearby")));
    }

    @Inject(
            method = "actionPerformed",
            at = @At(value = "RETURN")
    )
    private void betterRespawn$actionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == 2) {
            ((IBetterPlayerSP) this.mc.player).betterRespawn$respawnPlayer();
            this.mc.displayGuiScreen(null);
        }
    }
}
