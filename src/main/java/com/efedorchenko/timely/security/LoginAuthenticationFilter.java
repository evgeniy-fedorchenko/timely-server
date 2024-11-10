package com.efedorchenko.timely.security;

import com.efedorchenko.timely.entity.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginAuthenticationFilter extends OncePerRequestFilter {

    @Setter
    @NotNull
    private RequestMatcher requiresAuthenticationMatcher;

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final LoginAuthenticationConverter loginAuthenticationConverter;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (requiresAuthenticationMatcher != null && !requiresAuthenticationMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication authentication = loginAuthenticationConverter.convert(request);
            UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());

            if (!passwordEncoder.matches(authentication.getCredentials().toString(), userDetails.getPassword())) {
                if (log.isWarnEnabled()) {
                    log.warn("Authentication failed. Password does not match stored value. Username: {}",
                            authentication.getPrincipal());
                }
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            AuthenticationToken authenticatedToken = AuthenticationToken.authenticate(
                    userDetails.getAuthorities(), ((UserDetailsImpl) userDetails).getId()
            );
            SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
            filterChain.doFilter(request, response);

        } catch (UsernameNotFoundException | BadCredentialsException ex) {
            if (log.isWarnEnabled()) {
                log.warn("Authentication failed. Ex: {}", ex.getMessage());
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}