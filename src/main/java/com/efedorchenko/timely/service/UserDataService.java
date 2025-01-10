package com.efedorchenko.timely.service;

import com.efedorchenko.timely.model.data.UserDataModifyDto;
import com.efedorchenko.timely.model.data.UserDataType;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

// TODO 08.01.2025 19:16: Подумать над дженериками
public interface UserDataService<D, RANGE_REQ> {

    D addDataToOtherUser(UUID initiatorIdOfAdding, D dataDto);

    D addData(UUID userId, D dataDto);

    void deleteData(UUID userId, UUID targetUserId, UserDataType dataType, Long dataId);

    List<D> getRange(RANGE_REQ dataRangeRequest, UserDataType dataType);

    List<D> getUpdates(UUID userId, UserDataType dataType, @Nullable Instant since);

    void changeData(UUID userId, UserDataModifyDto newData);
}
