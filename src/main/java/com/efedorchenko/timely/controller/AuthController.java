package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.model.JwtTokenData;
import com.efedorchenko.timely.model.RegisterRequest;
import com.efedorchenko.timely.model.RegisterResponse;
import com.efedorchenko.timely.security.JwtUtil;
import com.efedorchenko.timely.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = AuthController.AUTH_ENDPOINT)
public class AuthController {

    public static final String AUTH_ENDPOINT = "/auth";

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping(path = "/login")
    public Mono<Void> login() {
        return Mono.empty();
    }

    @PostMapping(path = "/reg", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<RegisterResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return authService.register(registerRequest)
                .map(token -> token.getUserId() == null
                        ? RegisterResponse.fail()
                        : RegisterResponse.success(jwtUtil.generateToken(token))
                );
    }
}
