package com.efedorchenko.timely.logging;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class LogMethod {

    private static final String INPUT_PATTERN = "-> (%s)";
    private static final String OUTPUT_PATTERN = "<- (%s)";
    private static final String EX_PATTERN = "!- %s";

    private final ExecutorService executorOfVirtual;

    @Pointcut("@annotation(logAnnotation)")
    public void aroundLogPointcut(Log logAnnotation) {
    }

    @Around(value = "aroundLogPointcut(logAnnotation)", argNames = "joinPoint,logAnnotation")
    public Object aroundLog(ProceedingJoinPoint joinPoint, Log logAnnotation) throws Throwable {

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Logger logger = LoggerFactory.getLogger(method.getDeclaringClass().getName() + "." + method.getName());

        Level level = logAnnotation.level();
        boolean enabledForLevel = logger.isEnabledForLevel(level);

        if (enabledForLevel) {

            CompletableFuture.runAsync(() -> {

                String params = Arrays.stream(joinPoint.getArgs())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                logger.atLevel(level).log(INPUT_PATTERN.formatted(params));

            }, executorOfVirtual);
        }

        try {
            Object result = joinPoint.proceed();
                if (result instanceof CompletableFuture<?> future) {
                    future.whenComplete((res, ex) -> {
                        if (ex != null) {
                            String mess = EX_PATTERN.formatted(ex.toString());
                            logger.error(mess);
                        } else {
                            if (enabledForLevel) {
                                String mess = OUTPUT_PATTERN.formatted(res.toString());
                                logger.atLevel(level).log(mess);
                            }
                        }
                    });
                } else {
                    if (enabledForLevel) {
                        logger.atLevel(level).log(OUTPUT_PATTERN.formatted(result.toString()));
                    }
                }

            return result;

        } catch (Throwable ex) {
            String mess = EX_PATTERN.formatted(ex.toString());
            logger.atLevel(level).log(mess);

            throw ex;
        }
    }
}
