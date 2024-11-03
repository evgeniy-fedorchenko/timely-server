package com.efedorchenko.timely.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class DurationValidator implements ConstraintValidator<ValidDuration, Duration> {

    private long minSeconds;
    private long maxSeconds;

    @Override
    public void initialize(ValidDuration constraintAnnotation) {
        this.minSeconds = constraintAnnotation.min();
        this.maxSeconds = constraintAnnotation.max();
        ChronoUnit timeUnit = constraintAnnotation.timeUnit();

        if (timeUnit != ChronoUnit.SECONDS) {
            this.minSeconds = timeUnit.getDuration().multipliedBy(minSeconds).getSeconds();
            this.maxSeconds = timeUnit.getDuration().multipliedBy(maxSeconds).getSeconds();
        }
    }

    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext context) {
        if (duration == null) {
            return true;
        }

        long durationInSeconds = duration.getSeconds();
        return durationInSeconds >= minSeconds && durationInSeconds <= maxSeconds;
    }

}
