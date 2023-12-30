package net.jp.hellparadise.betterrespawn.helper;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.HoverEvent;

public class BetterTextComponentTranslation extends TextComponentTranslation {

    public BetterTextComponentTranslation(String translationKey, Object... args) {
        super(translationKey, args);
        this.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, this));
    }

}
