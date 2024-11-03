package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class TimelyControllerAdvice {

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<?>> handleException(Exception ex) {

        log.warn("Handle ex: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exName(ex.getClass().getName())
                .errorMessage(ex.getMessage())
                .build();

        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

}
