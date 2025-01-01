package com.efedorchenko.timely.model.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(builderClassName = "Builder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private static final AuthErrorCode DEFAULT_ERROR_CODE = AuthErrorCode.OK;
    private static final String DEFAULT_ERROR_MESSAGE = "OK";

    @lombok.Builder.Default
    private final boolean isRegister = true;

    /**
     * Данные авторизации: токены, ключи доступа и тд.
     * {@code null}, если запрос неудачный
     */
    @Nullable
    private final AuthData authData;

    /**
     * Данные юзера: имя, должность и тд.
     * {@code null}, если запрос неудачный
     */
    @Nullable
    private final UserData userData;

    /** Код ошибки авторизации */
    @lombok.Builder.Default
    private final AuthErrorCode errorCode = DEFAULT_ERROR_CODE;

    /** Пояснение ошибки авторизации */
    @lombok.Builder.Default
    private final String errorMessage = DEFAULT_ERROR_MESSAGE;

    public static AuthResponse failWith(AuthErrorCode authErrorCode) {
        return new AuthResponse(
                false,
                null,
                null,
                authErrorCode,
                authErrorCode.getDescription()
        );
    }
}
