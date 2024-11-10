package com.efedorchenko.timely.service;

import com.efedorchenko.timely.model.DataRangeRequest;
import com.efedorchenko.timely.model.EventsAndFines;
import com.efedorchenko.timely.model.UserDataType;

import java.util.UUID;

public interface UserDataService<T> {

    Void addData(UUID userId, T event);

    Void removeData(UUID userId, T userData);

    T getRange(UUID userId, DataRangeRequest dataRangeRequest, UserDataType dataType);

    EventsAndFines getRange(UUID userId, DataRangeRequest dataRangeRequest);
}
