package com.efedorchenko.timely.model.validation;

import com.efedorchenko.timely.exception.ErrorCode;
import com.efedorchenko.timely.exception.ServerException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.YearMonth;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.Temporal;

public class CurrentDatesValidator implements ConstraintValidator<CurrentDatesRange, Object> {

    private static final String CLASS_IS_NULL_MESS = "Annotated class must not be null";
    private static final String FIELD_IS_NULL_MESS = "One or both field names are null. Check 'startField' and 'endField' in the annotation";
    private static final String FIELD_NOT_FOUND_MESS_PATTERN = "One of the passed fields (%s or %s) was not found in the class %s";
    private static final String FAILED_UNKNOWN = "Validation failed for an unknown reason. Ex: ";

    private String startFieldName;
    private String endFieldName;

    @Override
    public void initialize(CurrentDatesRange constraintAnnotation) {
        startFieldName = constraintAnnotation.startField();
        endFieldName = constraintAnnotation.endField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        if (value == null) {
            failWithMessage(context, CLASS_IS_NULL_MESS);
            return false;
        }

        if (startFieldName == null || endFieldName == null) {
            failWithMessage(context, FIELD_IS_NULL_MESS);
            return false;
        }

        try {

            Object start = getFieldValue(value, startFieldName);
            Object end = getFieldValue(value, endFieldName);

            if (start instanceof YearMonth s && end instanceof YearMonth e) {
                return !s.isAfter(e) || createMessAndReturnFalse(s, e, context);
            } else if (start instanceof ChronoLocalDate s && end instanceof ChronoLocalDate e) {
                return !s.isAfter(e) || createMessAndReturnFalse(s, e, context);
            }

        } catch (NoSuchFieldException nsfe) {
            failWithMessage(context, FIELD_NOT_FOUND_MESS_PATTERN
                    .formatted(startFieldName, endFieldName, value.getClass().getName()));
        } catch (Exception ex) {
            throw new ServerException(ErrorCode.VALIDATION, FAILED_UNKNOWN, ex);
        }
        return false;
    }

    private Object getFieldValue(Object object, String fieldName) throws Exception {
        Class<?> clazz = object.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    private boolean createMessAndReturnFalse(Temporal start, Temporal end, ConstraintValidatorContext context) {
        String mess = context.getDefaultConstraintMessageTemplate().formatted(start, end);
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(mess);
        return false;
    }

    private void failWithMessage(ConstraintValidatorContext context, String messageTemplate) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(messageTemplate).addConstraintViolation();
    }
}
