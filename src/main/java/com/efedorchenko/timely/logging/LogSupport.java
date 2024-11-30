package com.efedorchenko.timely.logging;

import com.efedorchenko.timely.logging.Log.Ignore;
import jakarta.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class LogSupport {

    private static final Ignore.Mode[] DEFAULT_MODES
            = new Ignore.Mode[]{ Ignore.Mode.COMPLETION };

    private final Log logAnnotation;

    @Nullable
    private final Ignore ignoreMethodAnnotation;

    private final List<Ignore.Mode> methodIgnoreModes;

    LogSupport(Log logAnnotation, @Nullable Ignore ignoreMethodAnnotation) {
        this.logAnnotation = logAnnotation;
        this.ignoreMethodAnnotation = ignoreMethodAnnotation;

        if (ignoreMethodAnnotation == null) {
            this.methodIgnoreModes = Collections.emptyList();
        } else {
            this.methodIgnoreModes = !Arrays.equals(ignoreMethodAnnotation.value(), DEFAULT_MODES)
                    ? Arrays.asList(ignoreMethodAnnotation.value())
                    : Arrays.asList(ignoreMethodAnnotation.mode());
        }
    }

    boolean needsIgnoreInvoke() {
        return ignoreMethodAnnotation != null && methodIgnoreModes.contains(Ignore.Mode.INVOKE);
    }

    boolean needsIgnoreArguments() {
        return ignoreMethodAnnotation != null && methodIgnoreModes.contains(Ignore.Mode.ARGUMENTS);
    }

    boolean needsIgnoreCompletion() {
        return ignoreMethodAnnotation != null && methodIgnoreModes.contains(Ignore.Mode.COMPLETION);
    }

    boolean needsIgnoreReturnedValue() {
        return ignoreMethodAnnotation != null && methodIgnoreModes.contains(Ignore.Mode.RETURNED_VALUE);
    }

    boolean isLoggableEx(Throwable ex) {
        if (ignoreMethodAnnotation != null && !ignoreMethodAnnotation.ignoreEx()) {
            return true;
        }
        return Arrays.stream(logAnnotation.ex()).anyMatch(exClass -> exClass.isInstance(ex));
    }

    org.slf4j.event.Level getArgsLevel() {
        return logAnnotation.value() != Level.TRACE
                ? logAnnotation.value().getLevel()
                : logAnnotation.argsLevel().getLevel();
    }

    org.slf4j.event.Level getReturnLevel() {
        return logAnnotation.rtnLevel() != Level.USE_ARGS_LEVEL
                ? logAnnotation.rtnLevel().getLevel()
                : getArgsLevel();

    }

    org.slf4j.event.Level getExLevel() {
        return logAnnotation.exLevel().getLevel();
    }
}
