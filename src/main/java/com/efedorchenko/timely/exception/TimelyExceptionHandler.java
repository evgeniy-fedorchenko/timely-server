package com.efedorchenko.timely.exception;

import com.efedorchenko.timely.controller.AuthController;
import com.efedorchenko.timely.controller.DataController;
import com.efedorchenko.timely.logging.Level;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.logging.Log.Ignore.Mode;
import jakarta.annotation.Nullable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Логирование на уровне {@link org.slf4j.event.Level#WARN}, только возвращаемые значения.
 * Входные аргументы игнорируются, но логирование момента вызова метода происходит
 */
@Log(Level.WARN)
@Log.Ignore(Mode.ARGUMENTS)
@RestControllerAdvice(assignableTypes = { AuthController.class, DataController.class })
public class TimelyExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {

        if (ex.getCause() != null && ex.getCause() instanceof MethodArgumentNotValidException manve) {
            return handleNotValidArgumentEx(manve);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .exName(ex.getClass().getSimpleName())
                .sourceExName(getSourceExName(ex))
                .errorCode(ex.getErrorCode().getRawCode())
                .errorMessage(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ErrorResponse> handleServerException(ServerException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .exName(ex.getClass().getSimpleName())
                .sourceExName(getSourceExName(ex))
                .errorCode(ex.getErrorCode().getRawCode())
                .errorMessage(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleNotValidArgumentEx(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        String errorMessage = ("Validation failed for %d " + (errors.size() % 10 == 1 ? "argument" : "arguments"))
                .formatted(errors.size());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .exName(ex.getClass().getSimpleName())
                .errorCode(ErrorCode.VALIDATION.getRawCode())
                .errorMessage(errorMessage)
                .details(String.join(" | ", errors))
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler({AuthorizationDeniedException.class,
            AccessDeniedException.class,
            AuthenticationException.class})
    public ResponseEntity<?> handleAuthException(Exception ex) throws Exception {
        throw ex;   // Not handle, only standard logging
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .exName(ex.getClass().getName())
                .sourceExName(getSourceExName(ex))
                .errorMessage(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @Nullable
    private String getSourceExName(Exception ex) {
        return Optional.ofNullable(ex.getCause())
                .map(srcEx -> srcEx.getClass().getName())
                .orElse(null);
    }
}
