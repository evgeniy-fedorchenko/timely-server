package com.efedorchenko.timely.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Аннотация принимает на вход два поля (по их строковым именам) аннотированного класса
 * и при вызове валидации проверяет, что поле {@code endField} содержит дату/время, которое
 * на шкале времени находится не раньше, чем значение поля {@code startField}.
 * <p>
 * Следующие ситуации считаются недопустимыми и валидация не будет пройдена:
 * <lu>
 *     <li>Аннотированный класс не инициализирован</li>
 *     <li>Хотя бы одно переданное поле отсутствует в аннотированном классе</li>
 *     <li>Хотя бы одно из переданных полей является {@code null}</li>
 * </lu>
 *
 * @see CurrentDatesValidator
 * @see CurrentDatesRange.List
 */
@Target(TYPE)
@Retention(RUNTIME)
@Repeatable(CurrentDatesRange.List.class)
@Constraint(validatedBy = CurrentDatesValidator.class)
public @interface CurrentDatesRange {

    String startField();

    String endField();

    String message() default "The 'end' parameter value cannot be an earlier date than the 'start' parameter value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target(TYPE)
    @Retention(RUNTIME)
    @interface List {
        CurrentDatesRange[] value();
    }

}
