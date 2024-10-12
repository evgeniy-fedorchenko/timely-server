package com.efedorchenko.timely.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import reactor.util.annotation.Nullable;

import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private final AuthStatus status;

    @Nullable
    private final Role role;

    @Nullable
    @ToString.Exclude
    private final UUID uuid;

}
