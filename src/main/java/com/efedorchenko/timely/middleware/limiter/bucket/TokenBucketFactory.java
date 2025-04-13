package com.efedorchenko.timely.middleware.limiter.bucket;

import com.efedorchenko.timely.configuration.properties.LimitConfig;
import com.efedorchenko.timely.configuration.properties.RateLimiterProperties;
import com.efedorchenko.timely.middleware.limiter.RateLimiterEnabledOnCondition;
import com.efedorchenko.timely.middleware.limiter.RateLimitingStrategy;
import com.efedorchenko.timely.middleware.limiter.RateLimitingStrategyFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@RateLimiterEnabledOnCondition
public class TokenBucketFactory implements RateLimitingStrategyFactory<LimitType> {

    private final RateLimiterProperties properties;

    @Override
    public RateLimitingStrategy create(LimitType limitType) {
        LimitConfig config = properties.getLimit(limitType);
        if (config == null) {
            throw new IllegalArgumentException("No configuration found for limit type: " + limitType);
        }

        int capacity = getInitCapacity(config.getRefillIntervalMillis());
        return new TokenBucket(capacity, config.getRefillIntervalMillis());
    }

    /**
     * Рассчитывает изначальную емкость бакета, как количество
     * токенов, генерируемых в минуту, но минимум 1 токен
     */
    private int getInitCapacity(long refillIntervalMs) {
        return (int) Math.max(1, 60000 / refillIntervalMs);
    }
}
