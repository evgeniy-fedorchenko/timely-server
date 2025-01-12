package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.EntityType;
import com.efedorchenko.timely.entity.Event;
import com.efedorchenko.timely.model.data.UserDataType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@EntityType(UserDataType.EVENT)
public interface EventRepository extends UserDataRepository<Event> {

    @Override
    List<Event> findByUserIdAndMonthUidBetween(UUID userId, int monthUid, int monthUid2);

    @Override
    Optional<Event> findByUserIdAndDate(UUID userId, LocalDate date);

    @Override
    List<Event> getDataByUserIdAndChangedAtAfter(UUID userId, Instant changeAt);
}
