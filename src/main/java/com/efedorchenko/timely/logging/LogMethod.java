package com.efedorchenko.timely.logging;

import com.efedorchenko.timely.filter.RequestUidFilter;
import org.apache.logging.log4j.util.TriConsumer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class LogMethod {

    private final String INPUT_PATTERN = "-> (%s)";
    private final String OUTPUT_PATTERN = "<- (%s)";
    private final String EX_PATTERN = "!- %s";

    @Around("@annotation(logAnnotation)")
    public Mono<Object> logEndpoint(ProceedingJoinPoint joinPoint, Log logAnnotation) throws Throwable {
        try {

            return Mono.deferContextual(contextView -> {
                String rquid = contextView.getOrDefault(RequestUidFilter.RQUID, "NO_RqUID");
                Marker rquidMarker = MarkerFactory.getMarker(rquid);

                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                Logger logger = LoggerFactory.getLogger(method.getDeclaringClass().getName() + "." + method.getName());
                boolean enabledForLevel = logger.isEnabledForLevel(logAnnotation.level());

                if (enabledForLevel) {

                    Flux.fromIterable(Arrays.asList(joinPoint.getArgs()))
                            .map(Object::toString)
                            .collectList()
                            .map(list -> String.join(", ", list))
                            .doOnNext(str -> this.doLog(rquidMarker, INPUT_PATTERN.formatted(str), logAnnotation, logger))
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe();
                }

                Object result;
                try {
                    result = joinPoint.proceed();
                } catch (Throwable throwable) {
                    logger.error(rquidMarker, EX_PATTERN.formatted(throwable.getMessage()));
                    throw new ReactiveLogException(throwable);
                }

                if (logAnnotation.result() && enabledForLevel) {
                    if (result instanceof Mono<?> mono) {

                        return mono.flatMap(value -> {
                            this.doLog(rquidMarker, OUTPUT_PATTERN.formatted(value.toString()), logAnnotation, logger);
                            return Mono.just(value);

                        }).switchIfEmpty(Mono.fromRunnable(() -> this.doLog(
                                        rquidMarker, OUTPUT_PATTERN.formatted("empty"), logAnnotation, logger)
                                )
                        ).onErrorResume(ex -> {
                            this.doLog(rquidMarker, OUTPUT_PATTERN.formatted(ex.getMessage()), logAnnotation, logger);
                            return Mono.error(ex);
                        });

                    } else {
                        this.doLog(rquidMarker, OUTPUT_PATTERN.formatted(result.toString()), logAnnotation, logger);
                    }
                }
                return Mono.just(result);
            });

        } catch (ReactiveLogException rle) {
            throw rle.getCause();
        }
    }

    private void doLog(Marker rquidMarker, String logMessage, Log logAnnotation, Logger logger) {
        TriConsumer<Marker, String, String> loggingFunction = switch (logAnnotation.level()) {
            case TRACE -> logger::trace;
            case DEBUG -> logger::debug;
            case WARN -> logger::warn;
            case ERROR -> logger::error;
            default -> logger::info;
        };
        loggingFunction.accept(rquidMarker, logMessage, logMessage);
    }

    //    Для передачи исключения из лямбды
    private static final class ReactiveLogException extends RuntimeException {
        public ReactiveLogException(Throwable cause) {
            super(cause);
        }
    }
}
