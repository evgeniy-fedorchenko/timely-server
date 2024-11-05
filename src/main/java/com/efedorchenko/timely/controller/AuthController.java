package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.model.auth.AuthResponse;
import com.efedorchenko.timely.model.auth.JwtTokenData;
import com.efedorchenko.timely.model.auth.RegisterRequest;
import com.efedorchenko.timely.security.JwtUtil;
import com.efedorchenko.timely.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = AuthController.AUTH_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    public static final String AUTH_ENDPOINT = "/auth";

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Log
    @PostMapping(path = "/login")
    public Mono<ResponseEntity<AuthResponse>> login(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return authService.login(userDetails.getId())
                .map(this::generateAuthResponse)
                .map(r -> ResponseEntity.ok().body(r));
    }

    @Log
    @GetMapping(path = "/logout")
    public Mono<ResponseEntity<Void>> logout() {
        return Mono.empty(); // TODO 02.11.2024 22:49: реализовать logout
    }

    @Log
    @PostMapping(path = "/reg", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<AuthResponse>> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return authService.register(registerRequest)
                .map(this::generateAuthResponse)
                .map(r -> ResponseEntity.ok().body(r));
    }

    private AuthResponse generateAuthResponse(JwtTokenData token) {
        return token.getUserId() == null
                ? AuthResponse.failWith(token.getAuthFailReason())
                : AuthResponse.success(jwtUtil.generateToken(token), token.getAuthorities());
    }
}
