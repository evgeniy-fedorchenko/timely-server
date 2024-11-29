package com.efedorchenko.timely.entity;

import com.efedorchenko.timely.model.data.UserDataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityType {

    UserDataType value();

}
