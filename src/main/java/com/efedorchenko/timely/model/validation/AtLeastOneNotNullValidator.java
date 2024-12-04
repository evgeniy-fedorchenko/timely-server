package com.efedorchenko.timely.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;

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
                if (fieldValue == null || fieldValue instanceof String s && s.isEmpty()) {
                    continue;
                }
                return true;
            }
            setErrMessInContext(context, fieldNames);
            return false;

        } catch (NoSuchFieldException ex) {
            String errMess = "Validation of [%s] type filed: some of the fields %s do not found in the target class"
                    .formatted(value.getClass(), fieldNames);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errMess).addConstraintViolation();
            return false;

        } catch (Exception ex) {
            String errMessPattern = "Cannot validate fields {} at object of {} for an unknown reason. Returns that the field is invalid Ex: {}";
            log.error(errMessPattern, fieldNames, value.getClass(), ex.getMessage());
            return false;
        }
    }

    private void setErrMessInContext(ConstraintValidatorContext context, String... fieldNames) {
        String mess = context.getDefaultConstraintMessageTemplate().formatted(Arrays.asList(fieldNames));
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(mess).addConstraintViolation();
    }
}