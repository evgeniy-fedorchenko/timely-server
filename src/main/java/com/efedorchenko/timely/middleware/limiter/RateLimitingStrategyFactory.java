package com.efedorchenko.timely.middleware.limiter;

public interface RateLimitingStrategyFactory<T> {

    RateLimitingStrategy create(T identifier);
}
