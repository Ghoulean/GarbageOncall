package com.ghoulean.garbageoncall.handler;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.ghoulean.garbageoncall.accessor.DiscordAccessor;
import com.ghoulean.garbageoncall.accessor.DynamoDbAccessor;
import com.ghoulean.garbageoncall.model.OncallRotation;
import com.ghoulean.garbageoncall.model.RotationEntry;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class ChangeOncallHandler {
    private @NonNull final DynamoDbAccessor dynamoDbAccessor;
    private @NonNull final DiscordAccessor discordAccessor;
    private @NonNull final OncallRotation oncallRotation;

    @Inject
    public ChangeOncallHandler(@NonNull final DynamoDbAccessor dynamoDbAccessor,
            @NonNull final DiscordAccessor discordAccessor,
            @NonNull final OncallRotation oncallRotation) {
        this.dynamoDbAccessor = dynamoDbAccessor;
        this.discordAccessor = discordAccessor;
        this.oncallRotation = oncallRotation;
    }

    public void handle(final List<Integer> indices) {
        log.info("Handling ChangeOncallHandler");
        log.info("Calculating new entry");
        final RotationEntry newEntry = oncallRotation.fromIndices(indices);

        List<String> indicesStr = indices.stream()
                .map(i -> String.valueOf(i))
                .collect(Collectors.toList());
        log.info("Updating next oncall to: %s", newEntry.toString());

        dynamoDbAccessor.updateItemInTable(indicesStr);
        log.info("Successfully updated entry");

        final String discordMessage = prepareDiscordMessage(newEntry);
        log.info("Prepared discord message: ", discordMessage);
        discordAccessor.sendMessage(discordMessage);
        log.info("Sent message");
    }

    private String prepareDiscordMessage(final RotationEntry newOncall) {
        final List<String> message = new LinkedList<>();
        final String recyclingStr = newOncall.getRecycling() ? "also" : "no";

        message.add("**Next oncall has been manually updated**:");
        message.add(String.format("There is %s recycling for this week", recyclingStr));
        message.add(String.format("Next oncall: %s", newOncall.getPerson().getDisplayName()));
        return String.join("\n", message);
    }
}
