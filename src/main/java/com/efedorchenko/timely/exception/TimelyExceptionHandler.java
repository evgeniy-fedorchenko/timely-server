package com.efedorchenko.timely.exception;

import com.efedorchenko.timely.logging.Level;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.logging.Log.Ignore.Mode;
import jakarta.annotation.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Optional;

@Log(Level.WARN)
@Log.Ignore(Mode.ARGUMENTS)
@RestControllerAdvice
public class TimelyExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException ex) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exName(ex.getClass().getSimpleName())
                .sourceExName(getSourceExName(ex))
                .errorCode(ErrorCode.BUSINESS.getRawCode())
                .errorMessage(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<?> handleServerException(ServerException ex) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exName(ex.getClass().getSimpleName())
                .sourceExName(getSourceExName(ex))
                .errorCode(ErrorCode.SERVER.getRawCode())
                .errorMessage(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleNotValidArgumentEx(MethodArgumentNotValidException ex) {
        List<ArgInvalidDetails> details = ex.getFieldErrors().stream()
                .map(ArgInvalidDetails::new)
                .toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .exName(ex.getClass().getSimpleName())
                .errorCode(ErrorCode.VALIDATION.getRawCode())
                .errorMessage("Validation failed")
                .details(details)
                .build();

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {

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
