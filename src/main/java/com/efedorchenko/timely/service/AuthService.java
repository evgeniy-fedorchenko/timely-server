package com.efedorchenko.timely.service;

import com.efedorchenko.timely.model.auth.JwtTokenData;
import com.efedorchenko.timely.model.auth.RegisterRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AuthService {

    Mono<JwtTokenData> register(RegisterRequest registerRequest);

    Mono<JwtTokenData> login(UUID userId);

    Mono<Void> logout();
}
