package com.efedorchenko.timely.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegisterResponse {

    private final boolean isRegister;

    @Nullable
    @ToString.Exclude
    private final String jwtToken;

    public static RegisterResponse success(@NonNull String jwtToken) {
        return new RegisterResponse(true, jwtToken);
    }

    public static RegisterResponse fail() {
        return new RegisterResponse(false, null);
    }
}
