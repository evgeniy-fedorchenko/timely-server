package com.efedorchenko.timely.limiter;

public interface TokenBucket {

    /**
     * Пытается потребить токен из бакета
     * @return true если токен успешно потреблен, false если лимит исчерпан
     */
    boolean tryConsume();

    /**
     * Возвращает текущее количество доступных токенов
     */
    double getAvailableTokens();

    /**
     * Возвращает максимальную емкость бакета
     */
    int getCapacity();
}
