package com.efedorchenko.timely.configuration.properties;

import com.efedorchenko.timely.middleware.limiter.bucket.TokenBucket;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;
import java.util.Objects;

@Getter
@ToString
public class LimitConfig {

    private static final int DEFAULT_CACHE_SIZE = 50_000;
    private static final Duration DEFAULT_EXPIRE_AFTER = Duration.ofSeconds(1800);
    private static final int DEFAULT_REFILL_INTERVAL_MILLIS = 500;

    /**
     * Максимальное число одновременно хранимых клиентов. Указывает, сколько о каком количестве
     * клиентов информация о числе запросов может удерживаться одновременно. При достижении
     * лимита информация о наиболее старых клиентах будет удалена, а при их обращении повторно
     * инициализирована (вытесним других наиболее неактуальных клиентов).
     * Следует учитывать, что записи также будут удаляться по {@link LimitConfig#expireAfter}
     */
    @NotNull
    @Positive
    Integer cacheSize;

    /**
     * Указывает, через какое время запись будет удалена из кеша. Это время
     * обновляется при создании, обновлении или при любой операции чтения записи.
     * <p>
     * Технически, нет смысла устанавливать значение больше, чем время, за которое
     * может накопиться максимальное число токенов доступа (при использовании стратегии
     * {@code TokenBucket}), так как инициализация нового бакета - дешевая операция, разве
     * что это может быть немного полезно для разгрузки планировщика кеша
     */
    @NotNull
    @Positive
    Duration expireAfter;


    /**
     * Указывает, через какие интервалы времени будет генерироваться один токен доступа
     * (при использовании стратегии {@code TokenBucket}). Таким образом определяется разрешенная
     * частота запросов отдельного клиента. Конкретная реализация должна ограничить максимальное
     * количество генерируемых токенов
     * @see TokenBucket
     */
    @NotNull
    @Positive
    Integer refillIntervalMillis;

    public LimitConfig(@Nullable @Positive Integer cacheSize,
                       @Nullable Duration expireAfter,
                       @Nullable @Positive Integer refillIntervalMillis) {

        this.cacheSize = Objects.requireNonNullElse(cacheSize, DEFAULT_CACHE_SIZE);
        this.expireAfter = Objects.requireNonNullElse(expireAfter, DEFAULT_EXPIRE_AFTER);
        this.refillIntervalMillis = Objects.requireNonNullElse(refillIntervalMillis, DEFAULT_REFILL_INTERVAL_MILLIS);
    }

    public static LimitConfig getDefault() {
        return new LimitConfig(DEFAULT_CACHE_SIZE, DEFAULT_EXPIRE_AFTER, DEFAULT_REFILL_INTERVAL_MILLIS);
    }
}
