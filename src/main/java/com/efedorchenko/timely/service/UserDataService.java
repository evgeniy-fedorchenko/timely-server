package com.efedorchenko.timely.service;

import com.efedorchenko.timely.model.data.UserDataModifyDto;
import com.efedorchenko.timely.model.data.UserDataType;

import java.util.Collection;
import java.util.UUID;

public interface UserDataService<D, RANGE_REQ> {

    D addDataToOtherUser(UUID initiatorIdOfAdding, D userDataDto);

    D addData(UUID userId, D userDataDto);

    void deleteData(UUID userId, UUID clearableUserId, UserDataType userDataType, Long dataId);

    Collection<D> getRange(RANGE_REQ dataRangeRequest, UserDataType dataType);

    void changeData(UUID userId, UserDataModifyDto newData);
}
