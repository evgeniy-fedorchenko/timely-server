package com.efedorchenko.timely.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
 */
@Repeatable(CurrentDatesRange.List.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrentDatesValidator.class)
public @interface CurrentDatesRange {

    String startField();

    String endField();

    String message()
            default "The 'end' parameter value (%s) cannot be an earlier date than the 'start' parameter value (%s)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        CurrentDatesRange[] value();
    }

}
