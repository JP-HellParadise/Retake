package net.jp.hellparadise.retake;

import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MOD_ID, category = "Global config")
public class RetakeConfig {

    @Config.LangKey("retake.config.general.retry")
    @Config.RangeInt(min = 5, max = 50)
    public static int retryAttempt = 15;

    @Config.LangKey("retake.config.general.min")
    @Config.Comment("Default: 16")
    public static int minRadius = 16;

    @Config.LangKey("retake.config.general.max")
    @Config.Comment("Default: 32")
    public static int maxRadius = 32;

    @Config.LangKey("retake.config.general.cooldown")
    @Config.Comment({
        "Cooldown before allow next respawn (seconds)",
        "Default: 10"
    })
    public static int cooldown = 10;

    public static int cooldownAsTicks() {
        return cooldown * 20;
    }

    @Config.Name("debug")
    @Config.Comment({
        "Enable debug for cooldown counting (show per player)",
        "Default: false"
    })
    public static boolean debugCounter = false;

}
