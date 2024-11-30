package com.efedorchenko.timely.logging;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Level {

    TRACE(org.slf4j.event.Level.TRACE),
    DEBUG(org.slf4j.event.Level.DEBUG),
    INFO(org.slf4j.event.Level.INFO),
    WARN(org.slf4j.event.Level.WARN),
    ERROR(org.slf4j.event.Level.ERROR),
    USE_ARGS_LEVEL(null);

    private final org.slf4j.event.Level level;
}
