package com.efedorchenko.timely.limiter.atomic;

import com.efedorchenko.timely.configuration.properties.RateLimiterProperties;
import com.efedorchenko.timely.limiter.RateLimiterEnabledOnCondition;
import com.efedorchenko.timely.limiter.TokenBucket;
import com.efedorchenko.timely.limiter.TokenBucketFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@RateLimiterEnabledOnCondition
public class AtomicTokenBucketFactory implements TokenBucketFactory<LimitType> {

    private final RateLimiterProperties properties;

    @Override
    public TokenBucket create(LimitType limitType) {
        RateLimiterProperties.LimitConfig config = properties.getLimits().get(limitType);
        if (config == null) {
            throw new IllegalArgumentException("No configuration found for limit type: " + limitType);
        }

        int capacity = getInitCapacity(config.getRefillIntervalMillis());
        return new AtomicTokenBucket(capacity, config.getRefillIntervalMillis());
    }

    /**
     * Рассчитывает емкость бакета на основе интервала пополнения
     */
    private int getInitCapacity(long refillIntervalMs) {
        return (int) Math.max(1, 60000 / refillIntervalMs);
    }
}
