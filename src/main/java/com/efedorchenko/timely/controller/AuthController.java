package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.configuration.properties.ApplicationProperties;
import com.efedorchenko.timely.data.model.auth.AuthErrorCode;
import com.efedorchenko.timely.data.model.auth.AuthResponse;
import com.efedorchenko.timely.data.model.auth.RegisterRequest;
import com.efedorchenko.timely.middleware.logging.Level;
import com.efedorchenko.timely.middleware.logging.Log;
import com.efedorchenko.timely.security.LoginAuthenticationConverter;
import com.efedorchenko.timely.security.model.Credentials;
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

    /**
     * Используется {@link PostMapping} так как в запросе передается объект
     * {@link Credentials}, который потребляется в {@link LoginAuthenticationConverter}
     */
    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@AuthenticationPrincipal UUID userId) {
        return toResponseEntity(authService.login(userId));
    }

    @GetMapping(path = "/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @Log(Level.INFO)
    @PostMapping(path = "/reg", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        return toResponseEntity(authService.register(registerRequest));
    }

    private ResponseEntity<AuthResponse> toResponseEntity(AuthResponse response) {
        HttpStatus status = Optional.ofNullable(response.getErrorCode())
                .map(AuthErrorCode::getHttpStatus)
                .orElse(HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(status).body(response);
    }
}
