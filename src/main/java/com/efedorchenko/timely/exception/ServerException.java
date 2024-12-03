package com.efedorchenko.timely.exception;

import lombok.Getter;

/**
 * Ошибки в работе сервера:
 * <lu>
 *     <li>Сломалась логика</li>
 *     <li>Получен неожиданные результат, который невозможно обработать</li>
 * </lu>
 * и т.д, в идеальном мире это исключение не должно возникать никогда
 */
@Getter
public class ServerException extends RuntimeException {

    private final ErrorCode errorCode;

    public ServerException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ServerException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
