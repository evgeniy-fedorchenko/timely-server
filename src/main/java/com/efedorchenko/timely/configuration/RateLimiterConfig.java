package com.efedorchenko.timely.configuration;

import com.efedorchenko.timely.configuration.properties.LimitConfig;
import com.efedorchenko.timely.configuration.properties.RateLimiterProperties;
import com.efedorchenko.timely.middleware.limiter.RateLimiter;
import com.efedorchenko.timely.middleware.limiter.RateLimiterEnabledOnCondition;
import com.efedorchenko.timely.middleware.limiter.RateLimiterInvoker;
import com.efedorchenko.timely.middleware.limiter.RateLimitingStrategy;
import com.efedorchenko.timely.middleware.limiter.bucket.LimitType;
import com.efedorchenko.timely.middleware.limiter.bucket.TokenBucketRateLimiter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@AllArgsConstructor
@RateLimiterEnabledOnCondition
public class RateLimiterConfig {

    private final RateLimiterProperties rtProperties;

    /**
     * Регистратор {@link RateLimiterInvoker} в качестве обработчика входящего запроса.
     * <p>
     * Метод регистрирует один и тот же экземпляр {@link RateLimiterInvoker} как первый обработчик
     * в пайплайне обработки запроса томката сразу после снятия информации с сокета и создания
     * нормального java-объекта для анализа.
     * Это позволяет отбрасывать запросы на самом раннем этапе обработки, минимизирую нагрузку,
     * создаваемую запросами, которые не должны быть обработаны.
     * <p>
     * Длина стека вызовов в этом инвокере - 8 кадров. Для сравнения - перехват запроса Spring-фильтром
     * (установленным в самое начало цепочки) происходит на 71ом кадре. При обработке в контроллере - на 169ом.
     * Но последние два числа зависят еще от многих параметров и могут варьироваться
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> rateLimiterRegister(RateLimiter rateLimiter) {
        return factory -> factory.addEngineValves(new RateLimiterInvoker(rateLimiter));
    }

    /**
     * Создает карту кешей для использования в {@link TokenBucketRateLimiter}.
     * В качестве ключа выступает тип лимитирования. Значения - сами кеши, в которых ключи -
     * это хеши идентифицированных клиентов, а значения - стратегия лимитирования запросов
     */
    @Bean
    public Map<LimitType, Cache<Long, RateLimitingStrategy>> rateLimitCacheMap() {
        return Arrays.stream(LimitType.values())
                .collect(Collectors.toUnmodifiableMap(Function.identity(), this::createCache));
    }

    private Cache<Long, RateLimitingStrategy> createCache(LimitType type) {
        LimitConfig limitConfig = rtProperties.getLimit(type);
        return Caffeine.newBuilder()
                .maximumSize(limitConfig.getCacheSize())
                .expireAfterAccess(limitConfig.getExpireAfter())
                .recordStats()
                .build();
    }
}
