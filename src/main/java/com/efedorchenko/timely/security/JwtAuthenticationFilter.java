package com.efedorchenko.timely.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final RequestMatcher requiresAuthenticationMatcher;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (!requiresAuthenticationMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<String> rawTokenOpt = extract(request);
        if (rawTokenOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid JWT token");
            return;
        }

        String rawToken = rawTokenOpt.get();
        try {
            JwtUtil.RawAuthenticationData rawAuthData = jwtUtil.parseToken(rawToken);
            AuthenticationToken authenticationToken = AuthenticationToken.authenticate(
                    rawAuthData.getAuthorities(), UUID.fromString(rawAuthData.getUserId())
            );

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);

        } catch (BadCredentialsException bce) {
            if (log.isWarnEnabled()) {
                log.warn("Authentication failed for token: {}. Ip: {}. Ex: {}",
                        rawToken, request.getRemoteAddr(), bce.getMessage());
            }
            throw bce;
        }
    }

    private Optional<String> extract(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(authHeader -> authHeader.startsWith(BEARER_PREFIX))
                .map(rawAuthorizationHeaderValue -> rawAuthorizationHeaderValue.substring(BEARER_PREFIX.length()));
    }
}
