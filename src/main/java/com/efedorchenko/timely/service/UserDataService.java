package com.efedorchenko.timely.service;

import com.efedorchenko.timely.model.data.UserDataModifyDto;
import com.efedorchenko.timely.model.data.UserDataType;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserDataService<D, RANGE_REQ> {

    D addDataToOtherUser(UUID initiatorIdOfAdding, D userDataDto);

    D addData(UUID userId, D userDataDto);

    CompletableFuture<Collection<D>> getRange(
            UUID userId, RANGE_REQ dataRangeRequest, UserDataType dataType);

    void changeData(UUID userId, UserDataModifyDto newData);
}
