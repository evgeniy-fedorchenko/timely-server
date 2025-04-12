package com.efedorchenko.timely.limiter.atomic;

import com.efedorchenko.timely.limiter.TokenBucket;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicTokenBucket implements TokenBucket {

    private final int capacity;
    private final long refillIntervalMillis;
    private final AtomicLong lastRefillTimestamp;
    private final AtomicInteger tokens;

    public AtomicTokenBucket(int capacity, long refillIntervalMillis) {
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
            if (currentTokens <= 0) {
                return false;
            }
        } while (!tokens.compareAndSet(currentTokens, currentTokens - 1));
        return true;
    }

    @Override
    public int getAvailableTokens() {
        refill();
        return tokens.get();
    }

    @Override
    public int getCapacity() {
        return capacity;
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
