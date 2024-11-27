package com.efedorchenko.timely.model.data;

import com.efedorchenko.timely.entity.Event;
import com.efedorchenko.timely.entity.Fine;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventsAndFinesDto {

    @Nullable
    private final List<Event> events;

    @Nullable
    private final List<Fine> fines;
}
