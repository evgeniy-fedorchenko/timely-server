package com.efedorchenko.timely.model;

import com.efedorchenko.timely.entity.Event;
import com.efedorchenko.timely.entity.Fine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
public class EventsAndFines {

    private final List<Event> events;

    private final List<Fine> fines;
}
