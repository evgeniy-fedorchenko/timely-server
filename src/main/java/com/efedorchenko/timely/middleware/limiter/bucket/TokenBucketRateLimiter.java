package com.efedorchenko.timely.middleware.limiter.bucket;

import com.efedorchenko.timely.configuration.properties.ApplicationProperties;
import com.efedorchenko.timely.middleware.limiter.RateLimiter;
import com.efedorchenko.timely.middleware.limiter.RateLimiterEnabledOnCondition;
import com.efedorchenko.timely.middleware.limiter.RateLimitingStrategy;
import com.efedorchenko.timely.middleware.limiter.RateLimitingStrategyFactory;
import com.efedorchenko.timely.security.JwtUtil;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import net.jpountz.xxhash.XXHash64;
import net.jpountz.xxhash.XXHashFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
@RateLimiterEnabledOnCondition
public class TokenBucketRateLimiter implements RateLimiter {

    private static final XXHash64 XX_HASH_64 = XXHashFactory.fastestInstance().hash64();

    private static final List<String> AUTH_ENDPOINTS = List.of(
            ApplicationProperties.BASE_PATH + "/login",
            ApplicationProperties.BASE_PATH + "/logout",
            ApplicationProperties.BASE_PATH + "/reg"
    );

    private final RateLimitingStrategyFactory<LimitType> tokenBucketFactory;
    private final Map<LimitType, Cache<Long, RateLimitingStrategy>> rateLimitCacheMap;

    @Override
    public boolean isAllowed(HttpServletRequest request) {
        LimitType type = computeType(request);
        return rateLimitCacheMap.get(type)
                .get(createKey(type, request), k -> tokenBucketFactory.create(type))
                .tryConsume();
    }

    private Long createKey(LimitType type, HttpServletRequest request) {
        String value = switch (type) {
            case AUTH -> request.getRemoteAddr();
            case JWT -> JwtUtil.extractJwt(request).orElse(null);
        };
        return hash(value);
    }

    private Long hash(@Nullable String value) {
        return Optional.ofNullable(value)
                .map(notNullValue -> ByteBuffer.wrap(notNullValue.getBytes(StandardCharsets.UTF_8)))
                .map(buffer -> XX_HASH_64.hash(buffer, 0))
                .orElse(0L);
    }

    private LimitType computeType(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        for (String endpoint : AUTH_ENDPOINTS) {
            if (requestPath.equals(endpoint)) {
                return LimitType.AUTH;
            }
        }
        return LimitType.JWT;
    }
}
