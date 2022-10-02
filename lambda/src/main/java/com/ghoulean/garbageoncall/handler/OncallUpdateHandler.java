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
public final class OncallUpdateHandler {
    private @NonNull final DynamoDbAccessor dynamoDbAccessor;
    private @NonNull final DiscordAccessor discordAccessor;
    private @NonNull final OncallRotation oncallRotation;

    @Inject
    public OncallUpdateHandler(@NonNull final DynamoDbAccessor dynamoDbAccessor,
            @NonNull final DiscordAccessor discordAccessor,
            @NonNull final OncallRotation oncallRotation) {
        this.dynamoDbAccessor = dynamoDbAccessor;
        this.discordAccessor = discordAccessor;
        this.oncallRotation = oncallRotation;
    }

    public void handle() {
        log.info("Handling OncallUpdateHandler");
        log.info("Getting oncall indices from Dynamodb");
        final List<Integer> indices = dynamoDbAccessor.getItem().stream()
                .map(item -> Integer.valueOf(item))
                .collect(Collectors.toList());
        log.info("Got indices: ", indices);
        final RotationEntry currentEntry = oncallRotation.fromIndices(indices);
        log.info("Converted to RotationEntry: ", currentEntry);
        cycleIndices(indices);
        log.info("Next oncall indices: ", indices);
        final RotationEntry nextEntry = oncallRotation.fromIndices(indices);
        log.info("Converted to RotationEntry: ", nextEntry);

        final String discordMessage = prepareDiscordMessage(currentEntry, nextEntry);
        log.info("Prepared discord message: ", discordMessage);
        discordAccessor.sendMessage(discordMessage);
        log.info("Sent message");

        log.info("Updating Dynamodb entry");
        dynamoDbAccessor.updateItemInTable(indices.stream()
                .map(item -> String.valueOf(item))
                .collect(Collectors.toList()));
        log.info("Done update");
    }

    private void cycleIndices(final List<Integer> indices) {
        indices.set(0, (indices.get(0) + 1) % oncallRotation.getPersonSchedule().getSchedule().size());
        indices.set(1, (indices.get(1) + 1) % oncallRotation.getRecyclingSchedule().getSchedule().size());
    }

    private String prepareDiscordMessage(final RotationEntry currentOncall, final RotationEntry nextOncall) {
        final List<String> message = new LinkedList<>();
        final String currentPersonStr = currentOncall.getPerson().isPing()
                ? String.format("<@%s>", currentOncall.getPerson().getDiscordId())
                : currentOncall.getPerson().getDisplayName();
        final String recyclingStr = currentOncall.getRecycling() ? "also" : "no";

        message.add(String.format("**Reminder**: %s, you are oncall", currentPersonStr));
        message.add(String.format("There is %s recycling for this week", recyclingStr));
        message.add(String.format("Next oncall: %s", nextOncall.getPerson().getDisplayName()));
        return String.join("\n", message);
    }
}
