package com.efedorchenko.timely.model.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    private final String username;

    @NotBlank
    @ToString.Exclude
    private final String password;

    @NotNull
    private final List<Role> authorities;

    @NotNull
    @Size(max = 128)
    private final String name;

    @NotBlank
    @Size(max = 32)
    private final String position;

    @Positive
    private final int rate;

}
