package com.efedorchenko.timely.middleware.limiter;

import jakarta.servlet.http.HttpServletRequest;

public interface RateLimiter {

    boolean isAllowed(HttpServletRequest request);
}
