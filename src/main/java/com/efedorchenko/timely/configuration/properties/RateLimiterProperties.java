package com.efedorchenko.timely.configuration.properties;

import com.efedorchenko.timely.middleware.limiter.bucket.LimitType;
import jakarta.annotation.PostConstruct;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

@Slf4j
@Value
@Validated
@ConfigurationProperties(prefix = RateLimiterProperties.CONFIGURATION_PREFIX, ignoreUnknownFields = false)
public class RateLimiterProperties {

    public static final String CONFIGURATION_PREFIX = "rate-limiter";

    boolean enabled;

    Map<LimitType, LimitConfig> limits;

    public RateLimiterProperties(boolean enabled, Map<LimitType, LimitConfig> limits) {
        this.enabled = enabled;

        if (!enabled) {
            this.limits = Collections.emptyMap();
        } else {
            Map<LimitType, LimitConfig> tempMap = new EnumMap<>(LimitType.class);
            Arrays.stream(LimitType.values())
                    .forEach(type -> tempMap.put(type, limits.getOrDefault(type, LimitConfig.getDefault())));
            this.limits = Collections.unmodifiableMap(tempMap);
        }
    }

    public LimitConfig getLimit(LimitType limitType) {
        return limits.get(limitType);
    }

    @PostConstruct
    public void init() {
        if (enabled) {
            log.info("Rate-limiter enabled: {}", limits);
        }
    }
}
