package com.efedorchenko.timely.model;

import com.efedorchenko.timely.security.model.RoleType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaceMember {

    private final UUID userId;

    private final String name;

    private final String position;

    private final RoleType role;

    @Nullable
    private final Integer rate;

    private final Instant changedAt;
}
