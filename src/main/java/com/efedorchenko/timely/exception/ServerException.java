package com.efedorchenko.timely.exception;

/**
 * Ошибки в работе сервера:
 * <lu>
 *     <li>Сломалась логика</li>
 *     <li>Получен неожиданные результат, который невозможно обработать</li>
 * </lu>
 * и т.д, в идеальном мире это исключение не должно возникать никогда
 */
public class ServerException extends RuntimeException {

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(String message) {
        super(message);
    }

}
