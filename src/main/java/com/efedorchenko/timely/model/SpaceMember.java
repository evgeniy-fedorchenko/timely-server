package com.efedorchenko.timely.model;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@ToString
public class SpaceMember {

    private final UUID userId;

    private final String name;

    private final String position;

    @Nullable
    private final Integer rate;

    private final Instant changedAt;
}
