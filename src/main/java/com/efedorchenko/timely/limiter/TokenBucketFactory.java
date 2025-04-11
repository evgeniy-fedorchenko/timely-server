package com.efedorchenko.timely.limiter;

public interface TokenBucketFactory<T> {

    TokenBucket create(T identifier);
}
