package net.jp.hellparadise.retake;

import net.minecraftforge.common.config.Config;

@Config(modid = Tags.MOD_ID)
public class RetakeConfig {

    @Config.Name("Global config")
    public static Global global = new Global();

    public static class Global {

        private Global() {}

        @Config.LangKey("retake.config.general.retry")
        @Config.RangeInt(min = 1)
        public int retryAttempt = 25;

        @Config.LangKey("retake.config.general.min")
        @Config.Comment("Default: 16")
        public int minRadius = 16;

        @Config.LangKey("retake.config.general.max")
        @Config.Comment("Default: 32")
        public int maxRadius = 32;

        @Config.LangKey("retake.config.general.cooldown")
        @Config.Comment({
            "Cooldown before allow next respawn (seconds)",
            "Default: 10"
        })
        public int cooldown = 10;

        public int cooldownAsTicks() {
            return cooldown * 20;
        }

    }

    @Config.Name("Debug")
    public static Debug debug = new Debug();

    public static class Debug {

        private Debug() {}

        @Config.Comment({
            "Enable to see how long player has been on cooldown (show per player, in console)",
            "Default: false"
        })
        public boolean counter = false;

    }

    @Config.Name("Filter")
    public static Filter filter = new Filter();

    public static class Filter {

        private Filter() {}

        @Config.Comment({
            "Blocks that should not be allow to respawn on, use comma as the delimiter.",
            "Example: modid:block_name, modid2:block_name_2"
        })
        public String[] blacklistBlock = new String[]{};

    }

}
