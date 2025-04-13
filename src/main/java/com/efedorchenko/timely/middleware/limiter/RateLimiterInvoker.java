package com.efedorchenko.timely.middleware.limiter;

import jakarta.servlet.ServletException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
@RateLimiterEnabledOnCondition
public class RateLimiterInvoker extends ValveBase {

    private final RateLimiter rateLimiter;

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {

        if (rateLimiter.isAllowed(request)) {
            getNext().invoke(request, response);
        } else {
            log.warn("Rate limit exceeded for: [{}]", request.getRemoteAddr());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.finishResponse();
        }
    }
}
