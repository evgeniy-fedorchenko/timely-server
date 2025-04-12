package com.efedorchenko.timely.limiter.atomic;

import com.efedorchenko.timely.controller.AuthController;
import com.efedorchenko.timely.limiter.RateLimiter;
import com.efedorchenko.timely.limiter.RateLimiterEnabledOnCondition;
import com.efedorchenko.timely.limiter.TokenBucket;
import com.efedorchenko.timely.limiter.TokenBucketFactory;
import com.efedorchenko.timely.security.JwtUtil;
import com.github.benmanes.caffeine.cache.Cache;
import io.undertow.server.HttpServerExchange;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import net.jpountz.xxhash.XXHash64;
import net.jpountz.xxhash.XXHashFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@RateLimiterEnabledOnCondition
public class AtomicTokenBucketRateLimiter implements RateLimiter {

    private static final XXHash64 XX_HASH_64 = XXHashFactory.fastestInstance().hash64();

    private static final List<String> AUTH_ENDPOINTS = List.of(
            AuthController.AUTH_ENDPOINT + "/login",
            AuthController.AUTH_ENDPOINT + "/logout",
            AuthController.AUTH_ENDPOINT + "/reg"
    );

    private final TokenBucketFactory<LimitType> tokenBucketFactory;
    private final Map<LimitType, Cache<Long, TokenBucket>> rateLimitCacheMap;

    @Override
    public boolean isRateCheckPassed(HttpServerExchange exchange) {
        LimitType type = computeType(exchange);
        return rateLimitCacheMap.get(type)
                .get(createKey(type, exchange), k -> tokenBucketFactory.create(type))
                .tryConsume();
    }

    private Long createKey(LimitType type, HttpServerExchange exchange) {
        String value = switch (type) {
            case AUTH -> exchange.getSourceAddress().getAddress().getHostAddress();
            case JWT -> JwtUtil.extractJwt(exchange).orElse(null);
        };
        return hash(value);
    }

    private Long hash(@Nullable String value) {
        if (value == null) {
            return 0L;
        }
        ByteBuffer buffer = ByteBuffer.wrap(value.getBytes(StandardCharsets.UTF_8));
        return XX_HASH_64.hash(buffer, 0);
    }

    private LimitType computeType(HttpServerExchange exchange) {
        String requestPath = exchange.getRequestPath();
        if (AUTH_ENDPOINTS.stream().anyMatch(requestPath::equals)) {
            return LimitType.AUTH;
        }
        return LimitType.JWT;
    }
}
