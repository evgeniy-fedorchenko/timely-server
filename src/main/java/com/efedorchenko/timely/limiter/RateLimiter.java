package com.efedorchenko.timely.limiter;

import io.undertow.server.HttpServerExchange;


public class RateLimiter {

    public boolean isRateCheckPassed(HttpServerExchange exchange) {
        return true;
    }
}
