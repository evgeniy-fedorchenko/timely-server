package com.efedorchenko.timely.data.repository;

import com.efedorchenko.timely.data.entity.EntityType;
import com.efedorchenko.timely.data.entity.Fine;
import com.efedorchenko.timely.data.model.UserDataType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@EntityType(UserDataType.FINE)
public interface FineRepository extends UserDataRepository<Fine> {

    @Override
    List<Fine> findByUserIdAndMonthUidBetween(UUID userId, int monthUid, int monthUid2);

    @Override
    Optional<Fine> findByUserIdAndDate(UUID userId, LocalDate date);

    @Override
    List<Fine> getDataByUserIdAndChangedAtAfter(UUID userId, Instant changedAt);
}
