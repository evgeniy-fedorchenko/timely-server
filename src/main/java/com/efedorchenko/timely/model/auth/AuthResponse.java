package com.efedorchenko.timely.model.auth;

import com.efedorchenko.timely.model.SpaceKeys;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
@Builder(builderClassName = "Builder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private final boolean isRegister;

    @Nullable
    @ToString.Exclude
    private final String jwtToken;

    private final Set<RoleType> roles;

    @Nullable
    private final SpaceKeys generatedSpaceKeys;

    @Nullable
    private final AuthFailReason reason;

    @Nullable
    private final String errorMessage;

    public static AuthResponse failWith(AuthFailReason authFailReason) {
        return new AuthResponse(
                false,
                null,
                null,
                null,
                authFailReason,
                authFailReason.getDescription()
        );
    }
}
