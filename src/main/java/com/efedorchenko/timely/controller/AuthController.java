package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.model.auth.AuthResponse;
import com.efedorchenko.timely.model.auth.JwtTokenData;
import com.efedorchenko.timely.model.auth.RegisterRequest;
import com.efedorchenko.timely.security.JwtUtil;
import com.efedorchenko.timely.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = AuthController.AUTH_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    public static final String AUTH_ENDPOINT = "/auth";

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @PostMapping(path = "/login")
    public Mono<AuthResponse> login(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return authService.login(userDetails.getId()).map(generateAuthResponse());
    }

    @GetMapping(path = "/logout")
    public Mono<Void> logout() {
        return Mono.empty(); // TODO 02.11.2024 22:49: реализовать logout (для этого нужно ставить токенам время жизни)
    }

    @PostMapping(path = "/reg", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AuthResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return authService.register(registerRequest).map(generateAuthResponse());
    }

    private Function<JwtTokenData, AuthResponse> generateAuthResponse() {
        return token -> token.getUserId() == null
                ? AuthResponse.fail()
                : AuthResponse.success(jwtUtil.generateToken(token));
    }
}
