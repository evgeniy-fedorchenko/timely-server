package com.efedorchenko.timely.model.validation;

import com.efedorchenko.timely.entity.Event;
import com.efedorchenko.timely.entity.Fine;
import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.entity.UserEntity;

public class Constant {

    /**
     * <b>Regex:</b><br>
     * Допустимые символы: латиница, кириллица (любой регистр), цифры и символы {@code !@#$%^&*()_+-=[]{}|;:,.<>?\}<br>
     * <ul>
     *   <li>Длина: от 8 до 64 символов</li>
     *   <li>Минимум одна заглавная буква</li>
     *   <li>Минимум одна строчная буква</li>
     *   <li>Минимум одна цифра</li>
     *   <li>Запрещены пробелы</li>
     *   <li>Запрещено более трех одинаковых символов подряд</li>
     * </ul>
     */
    public static final String PASSWORD_REGEX =
            "^(?!.*(.)\\1{3,})(?=.*[A-ZА-Я])(?=.*[a-zа-я])(?=.*\\d)[A-Za-zА-Яа-я0-9!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?\\\\]{8,64}$";

    /**
     * Макс. длина комментария смены работника - поле {@link Event#comment} и для всех его {@code DTO}
     */
    public static final int COMMENT_MAX_LEN = 500;

    /**
     * Макс. длина описания штрафа - поле {@link Fine#description} и для всех его {@code DTO}
     */
    public static final int FINE_DESCRIPTION_MAX_LEN = 128;

    /**
     * Макс. длина емейла юзера - поле {@link UserDetailsImpl#username} и для всех его {@code DTO}
     */
    public static final int USERNAME_MAX_LEN = 128;

    /**
     * Макс. длина имени юзера - поле {@link UserEntity#name} и для всех его {@code DTO}
     */
    public static final int NAME_OF_USER_MAX_LEN = 255;

    /**
     * Макс. длина названия позиции работника поле {@link UserEntity#position} и для всех его {@code DTO}
     */
    public static final int USER_POSITION_MAX_LEN = 128;

    /**
     * Макс. длина ключа пространства, например {@link Space#workerKey} и для всех его {@code DTO}
     */
    public static final int SPACE_KEY_MAX_LEN = 36;

    /**
     * Макс. длина названия пространства, поле {@link Space#name} и для всех его {@code DTO}
     */
    public static final int SPACE_NAME_MAX_LEN = 128;
}
