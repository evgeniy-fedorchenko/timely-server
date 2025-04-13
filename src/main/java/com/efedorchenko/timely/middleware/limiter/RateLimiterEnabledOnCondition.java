package com.efedorchenko.timely.middleware.limiter;

import com.efedorchenko.timely.configuration.properties.RateLimiterProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ConditionalOnProperty(
        prefix = RateLimiterProperties.CONFIGURATION_PREFIX,
        name = "enabled",
        havingValue = "true"
)
public @interface RateLimiterEnabledOnCondition {
}
