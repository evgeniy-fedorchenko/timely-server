package com.efedorchenko.timely.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

@Slf4j
public class AtLeastOneNotNullValidator implements ConstraintValidator<AtLeastOneNotNull, Object> {

    private String[] fieldNames;

    @Override
    public void initialize(AtLeastOneNotNull constraintAnnotation) {
        fieldNames = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null || fieldNames.length == 0) {
            return false;
        }

        try {
            for (String fieldName : fieldNames) {
                Field field = value.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object fieldValue = field.get(value);

//                true - только если поле не null и не пустая строка
                if (fieldValue instanceof String s && s.isEmpty()) {
                    continue;
                }
                return true;
            }
            return false;

        } catch (NoSuchFieldException ex) {
            String mess = "Validation of [%s] type filed: some of the fields %s do not belong to the target class"
                    .formatted(value.getClass(), fieldNames);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(mess).addConstraintViolation();
            return false;

        } catch (Exception ex) {
            String errMessPattern = "Cannot validate fields {} at object of {} for an unknown reason. Returns that the field is valid Ex: {}";
            log.error(errMessPattern, fieldNames, value.getClass(), ex.getMessage());
            return true;
        }
    }

}