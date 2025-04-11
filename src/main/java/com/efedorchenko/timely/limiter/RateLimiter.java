package com.efedorchenko.timely.limiter;

import io.undertow.server.HttpServerExchange;

public interface RateLimiter {

    boolean isRateCheckPassed(HttpServerExchange exchange);
}
