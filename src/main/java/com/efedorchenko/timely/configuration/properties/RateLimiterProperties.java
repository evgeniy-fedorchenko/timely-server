package com.efedorchenko.timely.configuration.properties;

import com.efedorchenko.timely.limiter.atomic.LimitType;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

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

    @PostConstruct
    public void init() {
        if (enabled) {
            log.info("Rate-limiter enabled: {}", limits);
        }
    }

    @Getter
    public static class LimitConfig {

        private static final int DEFAULT_CACHE_SIZE = 50_000;
        private static final long DEFAULT_CACHE_EXPIRE_SECONDS = 1800;
        private static final int DEFAULT_REFILL_INTERVAL_MILLIS = 500;

        @NotNull
        @Positive
        Integer cacheSize;

        @NotNull
        @Positive
        Long cacheExpireSeconds;

        @NotNull
        @Positive
        Integer refillIntervalMillis;

        public LimitConfig(
                @Nullable Integer cacheSize, @Nullable Long cacheExpireSeconds, @Nullable Integer refillIntervalMillis) {

            this.cacheSize = Objects.requireNonNullElse(cacheSize, DEFAULT_CACHE_SIZE);
            this.cacheExpireSeconds = Objects.requireNonNullElse(cacheExpireSeconds, DEFAULT_CACHE_EXPIRE_SECONDS);
            this.refillIntervalMillis = Objects.requireNonNullElse(refillIntervalMillis, DEFAULT_REFILL_INTERVAL_MILLIS);
        }

        public static LimitConfig getDefault() {
            return new LimitConfig(DEFAULT_CACHE_SIZE, DEFAULT_CACHE_EXPIRE_SECONDS, DEFAULT_REFILL_INTERVAL_MILLIS);
        }
    }
}
