package com.efedorchenko.timely.security;

import com.efedorchenko.timely.exception.BusinessException;
import com.efedorchenko.timely.exception.ErrorCode;
import com.efedorchenko.timely.security.model.Credentials;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@AllArgsConstructor
public class LoginAuthenticationConverter implements AuthenticationConverter {

    private final Validator validator;
    private final ObjectMapper objectMapper;

    @Override
    public Authentication convert(HttpServletRequest request) {
        Credentials credentials = null;
        try {
            credentials = extractRequestBody(request);

            Set<ConstraintViolation<Credentials>> violations = validator.validate(credentials);
            if (!violations.isEmpty()) {
                if (log.isWarnEnabled()) {
                    log.warn("Filed validate authentication data\nAuthentication data: {}\nIp: {}\nViolations: {}",
                            credentials, request.getRemoteAddr(), violations);
                }
                throw new BusinessException(ErrorCode.VALIDATION,
                        "Failed validate authentication data. Violations: " + violations);
            }
            return new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());

        } catch (IOException ioex) {
            if (log.isWarnEnabled()) {
                log.warn("Convert authentication data failed\nAuthentication data: [{}]\nIp: {}. Ex: {}",
                        credentials, request.getRemoteAddr(), ioex.getMessage());
            }
            throw new BusinessException(ErrorCode.VALIDATION, "Invalid authentication data");
        }

    }

    private Credentials extractRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);

        char[] charBuffer = new char[1024];
        int bytesRead;
        while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
            stringBuilder.append(charBuffer, 0, bytesRead);
        }

        return objectMapper.readValue(stringBuilder.toString(), Credentials.class);
    }
}
