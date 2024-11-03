package com.efedorchenko.timely.model.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthResponse {

    private final boolean isRegister;

    @Nullable
    @ToString.Exclude
    private final String jwtToken;

    public static AuthResponse success(@NonNull String jwtToken) {
        return new AuthResponse(true, jwtToken);
    }

    public static AuthResponse fail() {
        return new AuthResponse(false, null);
    }
}
