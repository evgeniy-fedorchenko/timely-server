package com.efedorchenko.timely.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

/**
 * В БД храним {@link Event#workDuration} с точностью до минуты, так как более высокая точность ни к чему
 */
@Converter
public class WorkDurationConverter implements AttributeConverter<Duration, Long> {

    @Override
    public Long convertToDatabaseColumn(Duration attribute) {
        return attribute.toMinutes();
    }

    @Override
    public Duration convertToEntityAttribute(Long dbData) {
        return Duration.ofMinutes(dbData);
    }
}
