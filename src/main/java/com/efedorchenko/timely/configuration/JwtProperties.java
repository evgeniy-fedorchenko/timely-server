package com.efedorchenko.timely.configuration;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@AllArgsConstructor(onConstructor_ = @ConstructorBinding)
@ConfigurationProperties(prefix = JwtProperties.CONFIGURATION_PREFIX, ignoreUnknownFields = false)
public class JwtProperties {

    static final String CONFIGURATION_PREFIX = "timely.jwt";
    private static final String SECRET_REGEXP = "[\\w.]{32,256}";

    @NotNull
    @Pattern(regexp = SECRET_REGEXP)
    private final String secret;

    @NotNull
    @Positive
    private final Long expirationMillis;

}
