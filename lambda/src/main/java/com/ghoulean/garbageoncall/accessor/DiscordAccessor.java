package com.ghoulean.garbageoncall.accessor;

import javax.inject.Inject;
import javax.inject.Singleton;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.TextChannel;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public final class DiscordAccessor {
    private @NonNull final GatewayDiscordClient discordClient;
    private @NonNull final Snowflake channelId;

    @Inject
    public DiscordAccessor(@NonNull final GatewayDiscordClient discordClient, @NonNull final Snowflake channelId) {
        this.discordClient = discordClient;
        this.channelId = channelId;
    }

    public void sendMessage(@NonNull final String message) {
        log.info("sendMessage");
        discordClient.getChannelById(this.channelId)
            .ofType(TextChannel.class)
            .flatMap(channel -> channel.createMessage(message))
            .block();
    }
}
