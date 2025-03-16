package com.efedorchenko.timely.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
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

    @Nullable
    @Positive
    @JsonProperty("backendId")
    private final Long id;

    @NotNull
    private final LocalDate date;

    /**
     * {@code null} означает, что владелец данных - юзер, который авторизован в данный момент
     */
    @Nullable
    private final UUID owner;

    @Nullable
    private Instant deletedAt;

    @Nullable
    private Instant changedAt;

    @JsonIgnore
    public abstract UserDataType getType();
}
