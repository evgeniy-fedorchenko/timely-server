package com.efedorchenko.timely.limiter;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Setter
@Component
@RequiredArgsConstructor
@RateLimiterEnabledOnCondition
public class RateLimitHandler implements HttpHandler {

    private HttpHandler next;
    private final RateLimiter rateLimiter;

    @Override
    public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
        if (rateLimiter.isRateCheckPassed(httpServerExchange)) {
            next.handleRequest(httpServerExchange);
        } else {
            httpServerExchange.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServerExchange.endExchange();
        }
    }
}
