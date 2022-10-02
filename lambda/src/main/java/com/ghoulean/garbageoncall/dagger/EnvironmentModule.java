package com.ghoulean.garbageoncall.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import discord4j.common.util.Snowflake;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import software.amazon.awssdk.regions.Region;

@Module
@UtilityClass
@SuppressWarnings({"checkstyle:hideutilityclassconstructor"})
public final class EnvironmentModule {
    @Provides
    public static Region provideAwsRegion() {
        return Region.of(getEnv("AWS_REGION"));
    }

    @Named("tableName")
    @Provides
    public static String provideTableName() {
        return getEnv("TABLE_NAME");
    }

    @Named("discordToken")
    @Provides
    public static String provideDiscordToken() {
        return getEnv("DISCORD_TOKEN");
    }

    @Provides
    public static Snowflake provideDiscordChannelId() {
        return Snowflake.of(getEnv("DISCORD_CHANNEL_ID"));
    }

    private static String getEnv(@NonNull final String key) {
        return System.getenv(key);
    }
}
