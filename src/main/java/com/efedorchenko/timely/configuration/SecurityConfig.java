package com.efedorchenko.timely.configuration;

import com.efedorchenko.timely.security.JwtAuthenticationFilter;
import com.efedorchenko.timely.security.JwtUtil;
import com.efedorchenko.timely.security.LoginAuthenticationConverter;
import com.efedorchenko.timely.security.LoginAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    private static final String AUTH_PATHS = "/auth/**";
    private static final RequestMatcher ONLY_AUTH_MATCHER = new AntPathRequestMatcher(AUTH_PATHS);
    private static final RequestMatcher EXCEPT_AUTH_MATCHER = new NegatedRequestMatcher(ONLY_AUTH_MATCHER);

    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;
    private final LoginAuthenticationConverter authenticationConverter;

    @Bean
    public SecurityFilterChain baseSecurityFilterChain(HttpSecurity http) throws Exception {

        return http
                .securityMatcher(EXCEPT_AUTH_MATCHER)
                .authorizeHttpRequests(matcher -> matcher
                        .requestMatchers("swagger/**").hasRole(RoleType.MODERATOR.name())   // bean created only on dev profile
                        .requestMatchers("/actuator/**").hasRole(RoleType.MODERATOR.name())
                        .anyRequest().authenticated())

                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)

                .addFilterBefore(jwtAuthenticationFilter(), AnonymousAuthenticationFilter.class)
                .build();
    }

    @Bean
    public SecurityFilterChain authSecurityFilterChain(HttpSecurity http) throws Exception {

        return http
                .securityMatcher(ONLY_AUTH_MATCHER)
                .authorizeHttpRequests(matcher -> matcher
                        .requestMatchers("swagger/**").hasRole(RoleType.MODERATOR.name())   // bean created only on dev profile
                        .requestMatchers("/actuator/**").hasRole(RoleType.MODERATOR.name())
                        .requestMatchers(BASE_PATH + "/auth/reg").permitAll()
                        .requestMatchers(BASE_PATH + "/auth/login").authenticated()
                        .anyRequest().denyAll()
                )

                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)

                .addFilterBefore(loginAuthenticationFilter(), AnonymousAuthenticationFilter.class)
                .build();
    }

    @Bean
    protected JwtAuthenticationFilter jwtAuthenticationFilter() {
        JwtUtil jwtUtil = new JwtUtil(jwtProperties);
        return new JwtAuthenticationFilter(jwtUtil, EXCEPT_AUTH_MATCHER);
    }

    @Bean
    protected LoginAuthenticationFilter loginAuthenticationFilter() {
        LoginAuthenticationFilter loginAuthenticationFilter =
                new LoginAuthenticationFilter(passwordEncoder(), userDetailsService, authenticationConverter);

        AntPathRequestMatcher loginPathRequestMatcher = new AntPathRequestMatcher("/auth/login");
        loginAuthenticationFilter.setRequiresAuthenticationMatcher(loginPathRequestMatcher);
        return loginAuthenticationFilter;
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
