package com.efedorchenko.timely.security.model;

import com.efedorchenko.timely.model.SpaceKeys;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@Builder(builderClassName = "Builder")
public class AuthData {

    private final UUID userId;

    @ToString.Exclude
    private final String jwtToken;

    private final RoleType role;

    @Nullable
    private final SpaceKeys generatedSpaceKeys;
}
