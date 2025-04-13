package com.efedorchenko.timely.middleware.limiter.bucket;

import com.efedorchenko.timely.middleware.limiter.RateLimitingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Стратегия лимитирования запросов {@code TokenBucket}.
 * <p>
 * Стратегия поддерживает кратковременные скачки числа запросов, в пределах установленной {@code capacity}
 * Каждому клиенту выделяется {@code N} токенов в единицу времени, но при накоплении
 * они не превышают {@code capacity}. Потребление токенов происходит атомарно с использованием
 * стратегии {@code CAS} атомарных операций без синхронизированных блоков.
 * При инициализации бакет создается с количеством токенов, равным {@code capacity}
 */
@Slf4j
public class TokenBucket implements RateLimitingStrategy {

    private final int capacity;
    private final long refillIntervalMillis;
    private final AtomicLong lastRefillTimestamp;
    private final AtomicInteger tokens;

    public TokenBucket(int capacity, long refillIntervalMillis) {
        this.capacity = capacity;
        this.refillIntervalMillis = refillIntervalMillis;
        this.lastRefillTimestamp = new AtomicLong(System.currentTimeMillis());
        this.tokens = new AtomicInteger(capacity);
    }

    @Override
    public boolean tryConsume() {
        refill();
        int currentTokens;
        do {
            currentTokens = tokens.get();
            log.trace("Pre-consume, count: {}", currentTokens);
            if (currentTokens <= 0) {
                log.trace("consume filed. There are no tokens");
                return false;
            }
        } while (!tokens.compareAndSet(currentTokens, currentTokens - 1));
        log.trace("consumed 1 token, remaining tokens: {}", tokens.get());
        return true;
    }

    private void refill() {
        long currentTime = System.currentTimeMillis();
        long lastRefillTime = lastRefillTimestamp.get();
        long timeSinceLastRefill = currentTime - lastRefillTime;

        if (timeSinceLastRefill < refillIntervalMillis) {
            return;
        }

        long refillCount = timeSinceLastRefill / refillIntervalMillis;

        if (refillCount > 0) {
            long newValue = lastRefillTime + refillCount * refillIntervalMillis;

             /*
              * Если поток A успешно обновил lastRefillTimestamp, изменив значение с lastRefillTime на новое,
              * то когда поток B попытается выполнить ту же операцию с тем же ожидаемым значением lastRefillTime,
              * она обязательно вернет false, поскольку текущее значение уже не равно lastRefillTime
              */
            if (lastRefillTimestamp.compareAndSet(lastRefillTime, newValue)) {
                int tokensToAdd = (int) Math.min(capacity, refillCount);

                int currentTokens;
                int newTokens;
                do {
                    currentTokens = tokens.get();
                    newTokens = Math.min(capacity, currentTokens + tokensToAdd);
//                    Другой поток может потреблять токены в tryConsume()
                } while (!tokens.compareAndSet(currentTokens, newTokens));
            }
        }
    }
}
