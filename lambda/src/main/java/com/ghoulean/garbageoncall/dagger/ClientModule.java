package com.ghoulean.garbageoncall.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import lombok.NonNull;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Module
public final class ClientModule {
    @Provides
    public DynamoDbClient provideDynamoDB(@NonNull final Region region) {
        DynamoDbClient ddb = DynamoDbClient.builder()
            .region(region)
            .build();
       return ddb;
    }

    @Provides
    public GatewayDiscordClient provideDiscordClient(@Named("discordToken") final String token) {
        GatewayDiscordClient client = DiscordClient.builder(token)
            .build()
            .login()
            .block();
        return client;
    }
}
