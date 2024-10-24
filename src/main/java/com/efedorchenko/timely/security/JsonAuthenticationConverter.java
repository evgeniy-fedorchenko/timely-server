package com.efedorchenko.timely.security;

import com.efedorchenko.timely.model.AuthRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JsonAuthenticationConverter implements ServerAuthenticationConverter {

    private final Validator validator;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {

        return exchange.getRequest().getBody()
                .next()
                .flatMap(buffer -> {
                    try {
                        AuthRequest authRequest = objectMapper.readValue(buffer.asInputStream(), AuthRequest.class);
                        if (!validator.validate(authRequest).isEmpty()) {
                            return Mono.error(new BadCredentialsException("Invalid credentials"));
                        }
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());
                        return Mono.just(authenticationToken);

                    } catch (IOException ioe) {
                        return Mono.error(new AuthException("Invalid request payload", ioe));
                    }
                });
    }
}
