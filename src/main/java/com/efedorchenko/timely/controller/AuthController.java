package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.model.auth.AuthResponse;
import com.efedorchenko.timely.model.auth.JwtTokenData;
import com.efedorchenko.timely.model.auth.RegisterRequest;
import com.efedorchenko.timely.security.JwtUtil;
import com.efedorchenko.timely.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = AuthController.AUTH_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

    public static final String AUTH_ENDPOINT = "/auth";

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Log
    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@AuthenticationPrincipal UUID userId) {
        JwtTokenData jwtTokenData = authService.login(userId);
        return ResponseEntity.ok(generateAuthResponse(jwtTokenData));
    }

    @Log
    @GetMapping(path = "/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build(); // TODO 02.11.2024 22:49: реализовать logout
    }

    @Log
    @PostMapping(path = "/reg")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        JwtTokenData jwtTokenData = authService.register(registerRequest);
        return ResponseEntity.ok(generateAuthResponse(jwtTokenData));
    }

    private AuthResponse generateAuthResponse(JwtTokenData token) {
        return token.getUserId() == null
                ? AuthResponse.failWith(token.getAuthFailReason())
                : AuthResponse.success(jwtUtil.generateToken(token), token.getAuthorities());
    }
}
