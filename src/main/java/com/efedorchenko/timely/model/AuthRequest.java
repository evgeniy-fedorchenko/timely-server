package com.efedorchenko.timely.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Setter(AccessLevel.NONE)
public class AuthRequest {

    @NotBlank
    private final String username;

    @NotBlank
    @ToString.Exclude
    private final String password;

}
