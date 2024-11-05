package com.efedorchenko.timely.logging;

import org.slf4j.event.Level;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для логирования методов - их параметров и возвращаемого значения
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Log {

    @AliasFor("level")
    Level value() default Level.DEBUG;

    /**
     * Указывает, нужно ли включать возвращаемое значение в лог. По умолчанию {@code true}, то есть
     * возвращаемое значение будет залогировано. При отсутствии возвращаемого значения (в {@code void}
     * методах) любое значение параметра будет проигнорировано
     */
    boolean result() default true;

    /**
     * Уровень логирования для параметров и результата
     */
    @AliasFor("value")
    Level level() default Level.DEBUG;

}