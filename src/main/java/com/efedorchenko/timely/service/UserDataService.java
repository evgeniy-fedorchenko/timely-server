package com.efedorchenko.timely.service;

import com.efedorchenko.timely.model.data.UserDataModifyDto;
import com.efedorchenko.timely.model.data.UserDataType;

import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface UserDataService<DTO> {

    DTO addDataToOtherUser(UUID initiatorIdOfAdding, DTO dataDto);

    DTO addData(UUID userId, DTO dataDto);

    void deleteData(UUID userId, UUID targetUserId, UserDataType dataType, Long dataId);

    void changeData(UUID userId, UserDataModifyDto newData);

    List<DTO> getRange(UUID userId, YearMonth start, YearMonth end, UserDataType dataType);

    List<DTO> getUpdates(UUID userId, UserDataType dataType, Instant since);
}
