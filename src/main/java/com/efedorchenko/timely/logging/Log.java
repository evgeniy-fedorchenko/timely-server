package com.efedorchenko.timely.logging;

import org.slf4j.event.Level;

import java.lang.annotation.*;

/**
 * Аннотация для логирования методов - их параметров и возвращаемого значения
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Log {

    /**
     * Указывает, нужно ли включать возвращаемое значение в лог. По умолчанию {@code true}, то есть
     * возвращаемое значение будет залогировано. При отсутствии возвращаемого значения (в {@code void}
     * методах) любое значение параметра будет проигнорировано
     */
    boolean result() default true;

    /**
     * Уровень логирования для параметров и результата
     */
    Level level() default Level.DEBUG;

}