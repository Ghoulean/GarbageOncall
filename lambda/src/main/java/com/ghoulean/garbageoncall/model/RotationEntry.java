package com.ghoulean.garbageoncall.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class RotationEntry {
    private @NonNull final Person person;
    private @NonNull final Boolean recycling;
}
