package com.efedorchenko.timely.model;

import lombok.*;

@Data
@Setter(AccessLevel.NONE)
public class AuthRequest {

    private final String username;

    @ToString.Exclude
    private final String password;

}
