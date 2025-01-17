package com.efedorchenko.timely.model;

import com.efedorchenko.timely.security.model.RoleType;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaceConnectResponse {

    private final boolean success;

    @Nullable
    private final RoleType newRole;

    @Nullable
    private final SpaceDto space;

    public static SpaceConnectResponse fail() {
        return new SpaceConnectResponse(false, null, null);
    }

    public static SpaceConnectResponse success(RoleType newRole, SpaceDto space) {
        return new SpaceConnectResponse(true, newRole, space);
    }
}
