package com.efedorchenko.timely.service;

import com.efedorchenko.timely.model.DataRangeRequest;
import com.efedorchenko.timely.model.EventsAndFines;
import com.efedorchenko.timely.model.UserDataType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserDataService<T> {

    Mono<Void> addData(UUID userId, T event);

    Mono<Void> removeData(UUID userId, T userData);

    Flux<T> getRange(UUID userId, DataRangeRequest dataRangeRequest, UserDataType dataType);

    Mono<EventsAndFines> getRange(UUID userId, DataRangeRequest dataRangeRequest);
}
