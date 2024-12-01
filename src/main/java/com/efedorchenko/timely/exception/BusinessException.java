package com.efedorchenko.timely.exception;

/**
 * Исключения бизнес-логики:
 * <lu>
 *     <li>Не найден запрошенный ресурс</li>
 *     <li>Получены невалидные данные</li>
 * </lu>
 * и т.д.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}
