package com.efedorchenko.timely.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Аннотация для валидации промежутка времени, указанного в {@link java.time.Duration}
 * <p>
 * Проверяет, что указанное значение находится в диапазоне от {@code min} до {@code max}
 * временных единиц, указанных в {@code timeUnit}. Обе границы включительно
 * <p>
 * Значение {@code null} всегда считается валидным
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD, PARAMETER, LOCAL_VARIABLE })
@Constraint(validatedBy = DurationValidator.class)
public @interface ValidDuration {

    long min() default 8 * 3600;

    long max() default 24 * 3600;

    ChronoUnit timeUnit() default ChronoUnit.SECONDS;


    String message() default "Duration must be between {min} and {max} {timeUnit}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
