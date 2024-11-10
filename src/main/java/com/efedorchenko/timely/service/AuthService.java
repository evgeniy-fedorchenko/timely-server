package com.efedorchenko.timely.service;

import com.efedorchenko.timely.model.auth.JwtTokenData;
import com.efedorchenko.timely.model.auth.RegisterRequest;

import java.util.UUID;

public interface AuthService {

    JwtTokenData register(RegisterRequest registerRequest);

    JwtTokenData login(UUID userId);

    void logout();
}
