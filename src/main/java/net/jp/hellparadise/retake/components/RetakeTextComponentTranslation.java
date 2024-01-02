package net.jp.hellparadise.retake.components;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.HoverEvent;

@SuppressWarnings("unused")
public class RetakeTextComponentTranslation extends TextComponentTranslation {

    public RetakeTextComponentTranslation(String translationKey, Object... args) {
        super(translationKey, args);
        this.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, this));
    }

}
