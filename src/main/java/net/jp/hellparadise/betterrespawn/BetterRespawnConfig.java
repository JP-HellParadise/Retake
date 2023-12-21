package net.jp.hellparadise.betterrespawn;

import java.io.File;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

public class BetterRespawnConfig extends Configuration {
    private static BetterRespawnConfig instance;

    public static void init(final File configFile) {
        instance = new BetterRespawnConfig(configFile);
    }

    public static BetterRespawnConfig instance() {
        return instance;
    }

    public final int minRespawnRadius;
    public final int maxRespawnRadius;
    public final int retryAttempt;
    public final int respawnCooldown;

    private BetterRespawnConfig(File configFile) {
        super(configFile);

        MinecraftForge.EVENT_BUS.register(this);

        this.minRespawnRadius = this.get(
            "Respawn radius",
            "min",
            128,
            "The minimum distance to spawn the player away from its death location",
            0, 4096
            ).getInt();

        this.maxRespawnRadius = this.get(
            "Respawn radius",
            "max",
            256,
            "The minimum distance to spawn the player away from its death location",
            16, 8192
        ).getInt();

        this.retryAttempt = this.get(
            "Respawn mechanic",
            "retryAttempt",
            16,
            "Retry attempt (reduce if TPS lag)",
            2, 50
        ).getInt();

        this.respawnCooldown = this.get(
            "Respawn mechanic",
            "respawnCooldown",
            200,
            "If ya die within cooldown, the respawn nearby button will be disabled (use tick minecraft)",
            20, 6000
        ).getInt();
    }

    public static int clientRespawnCooldown = 0;
}
