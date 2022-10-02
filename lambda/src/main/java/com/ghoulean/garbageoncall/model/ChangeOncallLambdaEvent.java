package com.ghoulean.garbageoncall.model;

import lombok.Data;

@Data
public class ChangeOncallLambdaEvent {
    private Integer personIndex;
    private Integer recyclingIndex;
}
