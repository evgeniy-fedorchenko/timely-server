package com.efedorchenko.timely.model.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.ToString;

@Data
@Setter(AccessLevel.NONE)
public class AuthRequest {

    @NotBlank
    private final String username;

    @NotBlank
    @ToString.Exclude
    private final String password;

}
