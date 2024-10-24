package com.efedorchenko.timely.aop;

import com.efedorchenko.timely.filter.RequestUidFilter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class LogMethod {

    private final String INPUT_PATTERN =  "-> ({})";
    private final String OUTPUT_PATTERN = "<- ({})";
    private final String EX_PATTERN =     "!- {}";

    @Pointcut("execution(* com.efedorchenko.timely.controller.AuthController.*(..))")
    public void pointcutEndpoint() {
    }

    @Around("pointcutEndpoint()")
    public Mono<Object> logEndpoint(ProceedingJoinPoint joinPoint) throws Throwable {
        try {

            return Mono.deferContextual(contextView -> {
                String rquid = contextView.getOrDefault(RequestUidFilter.RQUID, "NO_RqUID");
                Marker rquidMarker = MarkerFactory.getMarker(rquid);

                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                String params = Arrays.stream(joinPoint.getArgs()).map(Object::toString).collect(Collectors.joining(", "));
                Logger log = LoggerFactory.getLogger(method.getDeclaringClass().getName() + "." + method.getName());

                log.info(rquidMarker, INPUT_PATTERN, params);

                Object result;
                try {
                    result = joinPoint.proceed();
                } catch (Throwable throwable) {
                    log.error(rquidMarker, EX_PATTERN, throwable.getMessage());
                    throw new ReactiveLogException(throwable);
                }

                if (result instanceof Mono<?> mono) {

                    return mono.flatMap(value -> {
                        log.info(rquidMarker, OUTPUT_PATTERN, value.toString());
                        return Mono.just(value);
                    }).onErrorResume(ex -> {
                        log.error(rquidMarker, OUTPUT_PATTERN, ex.getMessage());
                        return Mono.error(ex);
                    });

                } else {
                    log.info(rquidMarker, OUTPUT_PATTERN, result);
                    return Mono.just(result);
                }
            });

        } catch (ReactiveLogException rle) {
            throw rle.getCause();
        }
    }

//    Для передачи исключения из лямбды
    private static final class ReactiveLogException extends RuntimeException {
        public ReactiveLogException(Throwable cause) {
            super(cause);
        }
    }
}
