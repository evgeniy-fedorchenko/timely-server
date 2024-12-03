package com.efedorchenko.timely.model.auth;

import com.efedorchenko.timely.model.SpaceKeys;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@Builder(builderClassName = "Builder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private final boolean isRegister;

    @Nullable
    private final UUID userId;

    @Nullable
    @ToString.Exclude
    private final String jwtToken;

    @Nullable
    private final RoleType role;

    @Nullable
    private final SpaceKeys generatedSpaceKeys;

    @Nullable
    private final AuthErrorCode errorCode;

    @Nullable
    private final String errorMessage;

    public static AuthResponse failWith(AuthErrorCode authErrorCode) {
        return new AuthResponse(
                false,
                null,
                null,
                null,
                null,
                authErrorCode,
                authErrorCode.getDescription()
        );
    }
}
