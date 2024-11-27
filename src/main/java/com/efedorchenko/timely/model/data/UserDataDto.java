package com.efedorchenko.timely.model.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.annotation.Nullable;

import java.beans.Transient;
import java.time.LocalDate;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EventDto.class, name = EventDto.TYPE),
        @JsonSubTypes.Type(value = FineDto.class, name = FineDto.TYPE)
})
public abstract class UserDataDto {

    @Nullable
    public abstract Long getId();

    public abstract LocalDate getDate();

    @Transient
    public abstract UserDataType getType();
}
