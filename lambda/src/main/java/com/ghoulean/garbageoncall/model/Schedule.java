package com.ghoulean.garbageoncall.model;

import java.util.List;

import lombok.Data;
import lombok.NonNull;

@Data
public class Schedule<T> {
    private @NonNull final List<T> schedule;
}
