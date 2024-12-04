package com.efedorchenko.timely.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.beans.Transient;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@ToString
@SuperBuilder
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EventDto.class, name = EventDto.TYPE),
        @JsonSubTypes.Type(value = FineDto.class, name = FineDto.TYPE)
})
public abstract class UserDataDto {

    @Positive
    @Nullable
    private final Long id;

    @Future
    @NotNull
    private final LocalDate date;

    @Nullable
    private final UUID toUserId;

    @Transient
    @JsonIgnore
    public abstract UserDataType getType();
}
