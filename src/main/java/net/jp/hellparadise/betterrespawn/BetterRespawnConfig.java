package net.jp.hellparadise.betterrespawn;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class BetterRespawnConfig extends Configuration {
    private static BetterRespawnConfig instance;

    public static void init(final File configFile) {
        instance = new BetterRespawnConfig(configFile);
    }

    public static BetterRespawnConfig instance() {
        return instance;
    }

    private BetterRespawnConfig(File configFile) {
        super(configFile);

        MinecraftForge.EVENT_BUS.register(this);

        this.minRespawnRadius = this.get(
            "Respawn radius",
            "min",
            128,
            "The minimum distance to spawn the player away from its death location",
            0, 4096
            );

        this.maxRespawnRadius = this.get(
            "Respawn radius",
            "max",
            256,
            "The minimum distance to spawn the player away from its death location",
            16, 8192
        );

        this.retryAttempt = this.get(
            "Respawn mechanic",
            "retryAttempt",
            16,
            "Retry attempt (reduce if TPS lag)",
            2, 50
        );
    }



    private final Property minRespawnRadius;
    private final Property maxRespawnRadius;
    private final Property retryAttempt;

    public final int minRespawnRadius() {
        return minRespawnRadius.getInt();
    }
    public final int maxRespawnRadius() {
        return maxRespawnRadius.getInt();
    }
    public final int retryAttempt() {
        return retryAttempt.getInt();
    }

}
