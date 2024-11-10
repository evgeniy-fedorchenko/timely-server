package com.efedorchenko.timely.security;

import com.efedorchenko.timely.model.auth.AuthRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginAuthenticationConverter implements AuthenticationConverter {

    private final Validator validator;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication convert(HttpServletRequest request) {
        AuthRequest authRequest = null;
        try {
            authRequest = extractRequestBody(request);

            Set<ConstraintViolation<AuthRequest>> violations = validator.validate(authRequest);
            if (!violations.isEmpty()) {
                if (log.isWarnEnabled()) {
                    log.warn("Filed validate authentication data\nAuthentication data: {}\nIp: {}\nViolations: {}",
                            authRequest, request.getRemoteAddr(), violations);
                }
                throw new BadCredentialsException("Failed validate authentication data. Violations: " + violations);
            }
            return new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());

        } catch (IOException ioex) {
            if (log.isWarnEnabled()) {
                log.warn("Convert authentication data failed\nAuthentication data: {}\nIp: {}. Ex: {}",
                        authRequest, request.getRemoteAddr(), ioex.getMessage());
            }
            throw new BadCredentialsException("Invalid authentication data");
        }

    }

    private AuthRequest extractRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);

        char[] charBuffer = new char[1024];
        int bytesRead;
        while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
            stringBuilder.append(charBuffer, 0, bytesRead);
        }

        return objectMapper.readValue(stringBuilder.toString(), AuthRequest.class);
    }
}
