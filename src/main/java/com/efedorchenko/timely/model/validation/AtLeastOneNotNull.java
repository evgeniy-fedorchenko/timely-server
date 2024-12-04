package com.efedorchenko.timely.model.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Аннотация принимает на вход несколько полей (их строковых имен) аннотированного класса и при
 * вызове валидации проверяет, что ХОТЯ БЫ ОДНО из этих полей не равно {@code null} или пустой строке.
 * При использовании помните, что примитивные типы имеют дефолтные не-null значения
 * <p>
 * Следующие ситуации считаются недопустимыми и валидация не будет пройдена:
 * <lu>
 *     <li>Аннотация установлена, но ни одного поля не передано</li>
 *     <li>Аннотированный класс не инициализирован</li>
 *     <li>Переданное поле отсутствует в аннотированном классе</li>
 * </lu>
 *
 * @see AtLeastOneNotNullValidator
 * @see AtLeastOneNotNull.List
 */
@Target(TYPE)
@Retention(RUNTIME)
@Repeatable(AtLeastOneNotNull.List.class)
@Constraint(validatedBy = AtLeastOneNotNullValidator.class)
public @interface AtLeastOneNotNull {

    String[] value();

    String message() default "At least one field of %s must be not null";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target(TYPE)
    @Retention(RUNTIME)
    @interface List {
        AtLeastOneNotNull[] value();
    }

}
