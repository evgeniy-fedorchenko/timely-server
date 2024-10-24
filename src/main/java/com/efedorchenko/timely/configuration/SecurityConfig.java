package com.efedorchenko.timely.configuration;

import com.efedorchenko.timely.filter.JwtAuthenticationWebFilter;
import com.efedorchenko.timely.filter.RequestUidFilter;
import com.efedorchenko.timely.security.JsonAuthenticationConverter;
import com.efedorchenko.timely.security.JwtUtil;
import com.efedorchenko.timely.security.ReactiveUserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    private static final String AUTH_PATHS = "/auth/**";
    private static final ServerWebExchangeMatcher ONLY_AUTH_MATCHER =
            ServerWebExchangeMatchers.pathMatchers(AUTH_PATHS);
    private static final NegatedServerWebExchangeMatcher EXCEPT_AUTH_MATCHER =
            new NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers(AUTH_PATHS));

    private final JwtProperties jwtProperties;
    private final ReactiveUserDetailsServiceImpl userDetailsService;
    private final JsonAuthenticationConverter jsonAuthenticationConverter;

    @Bean
    public SecurityWebFilterChain baseSecurityFilterChain(ServerHttpSecurity http) {

        return http
                .securityMatcher(EXCEPT_AUTH_MATCHER)
                .authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())

                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .addFilterAt(requestUidFilter(), SecurityWebFiltersOrder.FIRST)
                .addFilterAt(jwtAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public SecurityWebFilterChain authSecurityFilterChain(ServerHttpSecurity http) {

        return http
                .securityMatcher(ONLY_AUTH_MATCHER)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/reg").permitAll()
                        .pathMatchers("/auth/login").authenticated()
                        .anyExchange().denyAll()
                )

                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .addFilterAt(requestUidFilter(), SecurityWebFiltersOrder.FIRST)
                .addFilterAt(userDetailsAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public JwtAuthenticationWebFilter jwtAuthenticationWebFilter() {
        JwtUtil jwtUtil = new JwtUtil(jwtProperties);
        return new JwtAuthenticationWebFilter(jwtUtil, EXCEPT_AUTH_MATCHER);
    }

    @Bean
    public AuthenticationWebFilter userDetailsAuthenticationWebFilter() {
        UserDetailsRepositoryReactiveAuthenticationManager authManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authManager.setPasswordEncoder(passwordEncoder());

        AuthenticationWebFilter authFilter = new AuthenticationWebFilter(authManager);
        authFilter.setServerAuthenticationConverter(jsonAuthenticationConverter);
        authFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/auth/login"));

        return authFilter;
    }

    @Bean
    public RequestUidFilter requestUidFilter() {
        return new RequestUidFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
