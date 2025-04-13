package com.efedorchenko.timely.limiter;

import jakarta.servlet.http.HttpServletRequest;

public interface RateLimiter {

    boolean isAllowed(HttpServletRequest request);
}
