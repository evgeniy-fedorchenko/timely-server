package com.efedorchenko.timely.model;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
public class AuthResponse {

    @Nullable
    @ToString.Exclude
    private final UUID uuid;

    private final AuthStatus status;

}
