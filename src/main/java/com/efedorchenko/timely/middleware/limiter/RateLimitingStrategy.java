package com.efedorchenko.timely.middleware.limiter;

public interface RateLimitingStrategy {

    /**
     * Пытается потребить токен из бакета
     * @return true если токен успешно потреблен, false если лимит исчерпан
     */
    boolean tryConsume();
}
