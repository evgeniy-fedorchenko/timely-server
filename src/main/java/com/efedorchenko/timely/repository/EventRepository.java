package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.EntityType;
import com.efedorchenko.timely.entity.Event;
import com.efedorchenko.timely.model.data.UserDataType;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@EntityType(UserDataType.EVENT)
public interface EventRepository extends UserDataRepository<Event> {

    @Override
    @Query("SELECT e FROM Event e WHERE e.user.id = :userId AND e.monthUid BETWEEN :monthUidStart AND :monthUidEnd")
    List<Event> getListOfUserData(UUID userId, int monthUidStart, int monthUidEnd);

    @Override
    Optional<Event> findByUserIdAndDate(UUID userId, LocalDate date);

    @Override
    List<Event> getDataByUserIdAndChangedAtBefore(UUID userId, Instant changeAt);
}
