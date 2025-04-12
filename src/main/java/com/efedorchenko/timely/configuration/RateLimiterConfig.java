package com.efedorchenko.timely.configuration;

import com.efedorchenko.timely.configuration.properties.RateLimiterProperties;
import com.efedorchenko.timely.limiter.RateLimiterEnabledOnCondition;
import com.efedorchenko.timely.limiter.TokenBucket;
import com.efedorchenko.timely.limiter.atomic.LimitType;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@AllArgsConstructor
@RateLimiterEnabledOnCondition
public class RateLimiterConfig {

    private final RateLimiterProperties rtProperties;

    @Bean
    public Map<LimitType, Cache<Long, TokenBucket>> rateLimitCacheMap() {
        return Arrays.stream(LimitType.values())
                .collect(Collectors.toUnmodifiableMap(Function.identity(), this::createCache));
    }

    private Cache<Long, TokenBucket> createCache(LimitType type) {
        RateLimiterProperties.LimitConfig limitConfig = rtProperties.getLimits().get(type);
        return Caffeine.newBuilder()
                .maximumSize(limitConfig.getCacheSize())
                .expireAfterAccess(limitConfig.getCacheExpireSeconds(), TimeUnit.SECONDS)
                .recordStats()
                .build();
    }
}
