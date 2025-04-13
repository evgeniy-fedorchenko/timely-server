package com.efedorchenko.timely.limiter;

public interface RateLimitingStrategyFactory<T> {

    RateLimitingStrategy create(T identifier);
}
