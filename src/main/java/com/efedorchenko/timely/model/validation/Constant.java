package com.efedorchenko.timely.model.validation;

public class Constant {

    /**
     * <b>Regex:</b><br>
     * Допустимые символы: латиница, кириллица (любой регистр), цифры и символы {@code !@#$%^&*()_+-=[]{}|;:,.<>?}<br>
     * Длина: от 8 до 32 символов
     * <p>
     * Требования:
     * <ul>
     *   <li>Минимум одна заглавная буква (A-Z или А-Я)</li>
     *   <li>Минимум одна цифра</li>
     * </ul>
     */
    public static final String PASSWORD_REGEX =
            "^(?=.*[A-ZА-Я])(?=.*\\d)[A-Za-zА-Яа-я0-9!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?]{8,32}$";

}
