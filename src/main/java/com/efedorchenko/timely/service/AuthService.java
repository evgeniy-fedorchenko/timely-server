package com.efedorchenko.timely.service;

import com.efedorchenko.timely.model.AuthRequest;
import com.efedorchenko.timely.model.AuthResponse;
import com.efedorchenko.timely.model.JwtTokenData;
import com.efedorchenko.timely.model.RegisterRequest;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<AuthResponse> authorise(AuthRequest authRequest);

    Mono<JwtTokenData> register(RegisterRequest registerRequest);
}
