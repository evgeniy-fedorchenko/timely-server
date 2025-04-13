package com.efedorchenko.timely.data.model.auth;

import com.efedorchenko.timely.security.model.AuthData;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(builderClassName = "Builder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private static final AuthErrorCode DEFAULT_ERROR_CODE = AuthErrorCode.OK;
    private static final String DEFAULT_ERROR_MESSAGE = DEFAULT_ERROR_CODE.getDescription();

    @Default
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
    @Default
    private final AuthErrorCode errorCode = DEFAULT_ERROR_CODE;

    /** Пояснение ошибки авторизации */
    @Default
    private final String errorMessage = DEFAULT_ERROR_MESSAGE;

    public static AuthResponse error(AuthErrorCode authErrorCode) {
        return AuthResponse.error(authErrorCode, authErrorCode.getDescription());
    }

    public static AuthResponse error(AuthErrorCode authErrorCode, String errorMessage) {
        return new AuthResponse(false, null, null, authErrorCode, errorMessage);
    }
}
