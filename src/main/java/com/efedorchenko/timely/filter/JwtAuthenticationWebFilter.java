package com.efedorchenko.timely.filter;

import com.efedorchenko.timely.security.AuthException;
import com.efedorchenko.timely.security.AuthenticationToken;
import com.efedorchenko.timely.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final ServerWebExchangeMatcher requiresAuthenticationMatcher;

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        return this.requiresAuthenticationMatcher.matches(exchange)
                .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
                .switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
                .flatMap(isMatch -> this.convertOrThrow(exchange.getRequest()))
                .flatMap(rawToken -> {
                    try {
                        JwtUtil.RawAuthenticationData rawAuthData = jwtUtil.parseToken(rawToken);
                        AuthenticationToken authenticationToken =
                                AuthenticationToken.authenticate(rawAuthData.getAuthorities(), rawAuthData.getUserId());

                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticationToken));
                    } catch (AuthenticationException ae) {
                        return Mono.error(ae);
                    }
                })

                .onErrorResume(ex -> {
                    exchange.getResponse().setStatusCode(
                            ex instanceof AuthException ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR
                    );
                    return exchange.getResponse().setComplete();
                });
    }

    private Mono<String> convertOrThrow(ServerHttpRequest request) throws AuthException {
        return Mono.justOrEmpty(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .switchIfEmpty(Mono.error(new AuthException()))
                .filter(authHeader -> authHeader.startsWith(BEARER_PREFIX))
                .switchIfEmpty(Mono.error(new AuthException()))
                .map(rawAuthorizationHeaderValue -> rawAuthorizationHeaderValue.substring(BEARER_PREFIX.length()));
    }

}
