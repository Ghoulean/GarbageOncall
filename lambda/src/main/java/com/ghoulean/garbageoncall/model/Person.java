package com.ghoulean.garbageoncall.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Person {
    private @NonNull final String discordId;
    private @NonNull final String displayName;
    private final boolean ping;
}
