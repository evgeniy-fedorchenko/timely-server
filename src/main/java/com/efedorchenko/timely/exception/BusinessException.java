package com.efedorchenko.timely.exception;

import lombok.Getter;

/**
 * Исключения бизнес-логики:
 * <lu>
 *     <li>Не найден запрошенный ресурс</li>
 *     <li>Получены невалидные данные</li>
 * </lu>
 * и т.д.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
