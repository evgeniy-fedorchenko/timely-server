package com.efedorchenko.timely.logging;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.efedorchenko.timely.logging.Log.Ignore;
import static com.efedorchenko.timely.logging.Log.IgnoreAll;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
class LogMethod {

    private static final String INPUT_PATTERN = "-> (%s)";
    private static final String OUTPUT_PATTERN = "<- (%s)";
    private static final String EX_PATTERN = "!- %s";
    private static final String EMPTY_STRING = "";

    private final ExecutorService executorOfVirtual;

    @Pointcut("@annotation(log) || @within(log)")
    void logPointcut(Log log) {
    }

    @Around(value = "logPointcut(log)", argNames = "joinPoint,log")
    Object logAround(ProceedingJoinPoint joinPoint, Log log) throws Throwable {

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        if (method.isAnnotationPresent(IgnoreAll.class)) {
            return joinPoint.proceed();
        }

        Logger logger = getLogger(method);
        Log logAnnotation = Optional.ofNullable(method.getAnnotation(Log.class)).orElse(log);
        LogAnnotationSupport logSupport = new LogAnnotationSupport(logAnnotation, method);
        boolean enabledForLevel = logger.isEnabledForLevel(logSupport.getArgsLevel());

        if (enabledForLevel && !logSupport.needsIgnoreInvoke()) {
            CompletableFuture.runAsync(
                    () -> logArguments(joinPoint.getArgs(), method.getParameters(), logger, logSupport),
                    executorOfVirtual
            );
        }

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable t) {
            doLogResult(t, logger, logSupport);
            throw t;
        }

        if (result instanceof CompletableFuture<?> future) {
            future.whenComplete((res, ex) -> doLogResult((ex != null ? ex : res), logger, logSupport));

        } else if (method.getReturnType().equals(Void.TYPE)) {
            doLogResult(EMPTY_STRING, logger, logSupport);

        } else {
            doLogResult(result, logger, logSupport);
        }

        return result;
    }

    private void logArguments(Object[] args, Parameter[] sourceParams, Logger logger, LogAnnotationSupport logSupport) {
        try {

            if (logSupport.needsIgnoreArguments()) {
                logger.atLevel(logSupport.getArgsLevel()).log(INPUT_PATTERN.formatted(EMPTY_STRING));
                return;
            }

            List<Object> loggableArgs = new ArrayList<>();
            IntStream.range(0, args.length).forEach(idx -> {
                        boolean isNotIgnoredParameter = Arrays.stream(sourceParams[idx].getAnnotations())
                                .filter(a -> a.annotationType().equals(Ignore.class))
                                .findFirst()
                                .isEmpty();
                        if (isNotIgnoredParameter) {
                            loggableArgs.add(args[idx]);
                        }
                    }
            );
            String params = loggableArgs.isEmpty()
                    ? EMPTY_STRING
                    : loggableArgs.stream()
                    .map(arg -> arg != null ? arg.toString() : "null")
                    .collect(Collectors.joining(", "));

            logger.atLevel(logSupport.getArgsLevel()).log(INPUT_PATTERN.formatted(params));
        } catch (Throwable t) {
            log.error("Cannot logging parameters of method [{}]. Ex: {}", logger.getName(), t.getMessage());
        }
    }

    private void doLogResult(Object loggingObj, Logger logger, LogAnnotationSupport logSupport) {
        try {

            if (loggingObj instanceof Throwable t
                    && logger.isEnabledForLevel(logSupport.getExLevel())
                    && logSupport.isLoggableEx(t)) {

                String mess = EX_PATTERN.formatted(t.toString());
                logger.atLevel(logSupport.getExLevel()).log(mess);

            } else if (logger.isEnabledForLevel(logSupport.getExLevel())) {
                if (logSupport.needsIgnoreCompletion()) {
                    return;

                } else if (logSupport.needsIgnoreReturnedValue()) {
                    logger.atLevel(logSupport.getReturnLevel()).log(OUTPUT_PATTERN.formatted(EMPTY_STRING));
                    return;
                }
                String mess = OUTPUT_PATTERN.formatted(loggingObj != null ? loggingObj.toString() : "null");
                logger.atLevel(logSupport.getReturnLevel()).log(mess);
            }

        } catch (Throwable t) {
            log.error("Cannot logging result of method [{}]. Ex: {}", logger.getName(), t.getMessage());
        }
    }

    private Logger getLogger(Method method) {
        String name = String.join(".", method.getDeclaringClass().getName(), method.getName());
        return LoggerFactory.getLogger(name);
    }
}
