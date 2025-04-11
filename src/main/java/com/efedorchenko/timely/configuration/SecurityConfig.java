package com.efedorchenko.timely.configuration;

import com.efedorchenko.timely.configuration.properties.JwtProperties;
import com.efedorchenko.timely.security.JwtAuthenticationFilter;
import com.efedorchenko.timely.security.JwtUtil;
import com.efedorchenko.timely.security.LoginAuthenticationConverter;
import com.efedorchenko.timely.security.LoginAuthenticationFilter;
import com.efedorchenko.timely.security.model.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import static com.efedorchenko.timely.configuration.properties.ApplicationProperties.BASE_PATH;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final RequestMatcher BASE_MATCHER = new AntPathRequestMatcher(BASE_PATH + "/**");
    public static final RequestMatcher ONLY_AUTH_MATCHER = new AntPathRequestMatcher(BASE_PATH + "/auth/**");
    public static final RequestMatcher EXCEPT_AUTH_MATCHER =
            request -> BASE_MATCHER.matches(request) && !ONLY_AUTH_MATCHER.matches(request);

    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;
    private final LoginAuthenticationConverter authenticationConverter;

    @Bean
    public SecurityFilterChain baseSecurityFilterChain(HttpSecurity http) throws Exception {

        return http
                .securityMatcher(EXCEPT_AUTH_MATCHER)
                .authorizeHttpRequests(matcher -> matcher
                        .requestMatchers("/swagger/**").hasRole(RoleType.MODERATOR.name())   // bean created only on dev profile
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
                        .requestMatchers("/swagger/**").hasRole(RoleType.MODERATOR.name())   // bean created only on dev profile
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

        AntPathRequestMatcher loginPathRequestMatcher = new AntPathRequestMatcher(BASE_PATH + "/auth/login");
        loginAuthenticationFilter.setRequiresAuthenticationMatcher(loginPathRequestMatcher);
        return loginAuthenticationFilter;
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
