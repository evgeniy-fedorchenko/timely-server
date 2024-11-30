package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.configuration.ApplicationProperties;
import com.efedorchenko.timely.logging.Level;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.model.auth.AuthFailReason;
import com.efedorchenko.timely.model.auth.AuthResponse;
import com.efedorchenko.timely.model.auth.RegisterRequest;
import com.efedorchenko.timely.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log(Level.DEBUG)
@Validated
@AllArgsConstructor
@RestController
@RequestMapping(path = AuthController.AUTH_ENDPOINT, produces = APPLICATION_JSON_VALUE)
public class AuthController {

    public static final String AUTH_ENDPOINT = ApplicationProperties.BASE_PATH + "/auth";

    private final AuthService<RegisterRequest, AuthResponse> authService;

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@AuthenticationPrincipal UUID userId) {
        return computeResponseEntity(authService.login(userId));
    }

    @GetMapping(path = "/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @Log(Level.INFO)
    @PostMapping(path = "/reg", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return computeResponseEntity(authService.register(registerRequest));
    }

    private ResponseEntity<AuthResponse> computeResponseEntity(AuthResponse response) {
        if (response.isRegister()) {
            return ResponseEntity.ok(response);
        } else {
            HttpStatus status = Optional.ofNullable(response.getReason())
                    .map(AuthFailReason::getHttpStatus)
                    .orElse(HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(status).body(response);
        }
    }
}
