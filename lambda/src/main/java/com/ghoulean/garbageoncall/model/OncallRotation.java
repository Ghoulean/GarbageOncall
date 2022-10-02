package com.ghoulean.garbageoncall.model;

import java.util.List;

import lombok.Data;
import lombok.NonNull;

@Data
public class OncallRotation {
    private @NonNull final Schedule<Person> personSchedule;
    private @NonNull final Schedule<Boolean> recyclingSchedule;

    public final RotationEntry fromIndices(final List<Integer> indices) {
        return RotationEntry.builder()
            .person(personSchedule.getSchedule().get(indices.get(0)))
            .recycling(recyclingSchedule.getSchedule().get(indices.get(1)))
            .build();
    }
}
