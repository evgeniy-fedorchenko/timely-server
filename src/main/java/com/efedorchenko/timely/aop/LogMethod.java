package com.efedorchenko.timely.aop;

import com.efedorchenko.timely.filter.MdcFilter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class LogMethod {

    @Pointcut("execution(* com.efedorchenko.timely.controller.AuthController.*(..))")
    public void pointcutEndpoint() {
    }

    @Around("pointcutEndpoint()")
    public Mono<Object> logEndpoint(ProceedingJoinPoint joinPoint) throws Throwable {
        try {

            return Mono.deferContextual(contextView -> {
                String rquid = contextView.getOrDefault(MdcFilter.RQUID, "NO_RqUID");
                Marker rquidMarker = MarkerFactory.getMarker(rquid);

                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                String params = Arrays.stream(joinPoint.getArgs()).map(Object::toString).collect(Collectors.joining(", "));
                log.info(rquidMarker, "-> [{}.{}] - ({})", method.getDeclaringClass().getSimpleName(), method.getName(), params);

                Object result;
                try {
                    result = joinPoint.proceed();
                } catch (Throwable throwable) {
                    log.error(rquidMarker, "!- {}", throwable.getMessage());
                    throw new ReactiveLogException(throwable);
                }

                if (result instanceof Mono<?> mono) {

                    return mono.flatMap(value -> {
                        log.info(rquidMarker, "<- ({})", value.toString());
                        return Mono.just(value);
                    }).onErrorResume(ex -> {
                        log.error(rquidMarker, "<- ({})", ex.getMessage());
                        return Mono.error(ex);
                    });

                } else {
                    log.info(rquidMarker, "<- ({})", result);
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
