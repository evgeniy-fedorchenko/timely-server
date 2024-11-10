package com.efedorchenko.timely.model.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private final boolean isRegister;

    @Nullable
    @ToString.Exclude
    private final String jwtToken;

    private final List<Role> authorities;

    @Nullable
    private final AuthFailReason authFailReason;

    public static AuthResponse success(@NotNull String jwtToken, List<Role> authorities) {
        return new AuthResponse(true, jwtToken, authorities, null);
    }

    public static AuthResponse failWith(AuthFailReason authFailReason) {
        return new AuthResponse(false, null, null, authFailReason);
    }
}
