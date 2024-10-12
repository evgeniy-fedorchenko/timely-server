package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.model.AuthRequest;
import com.efedorchenko.timely.model.AuthResponse;
import com.efedorchenko.timely.model.AuthStatus;
import com.efedorchenko.timely.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

@RequiredArgsConstructor
@RestController
@RequestMapping(
        path = AuthController.AUTH_ENDPOINT,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class AuthController {

    static final String AUTH_ENDPOINT = "/auth";

    private final AuthService authService;

    @PostMapping(path = "/login")
    public Mono<AuthResponse> login(@RequestBody @NonNull AuthRequest authRequest) {
        return authService.authorise(authRequest)
                .map(uuid -> new AuthResponse(uuid, AuthStatus.SUCCESS))
                .defaultIfEmpty(new AuthResponse(null, AuthStatus.FAIL));
    }
}
