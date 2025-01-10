package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.EntityType;
import com.efedorchenko.timely.entity.Fine;
import com.efedorchenko.timely.model.data.UserDataType;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@EntityType(UserDataType.FINE)
public interface FineRepository extends UserDataRepository<Fine> {

    @Override
    @Query("SELECT f FROM Fine f WHERE f.user.id = :userId AND f.monthUid BETWEEN :monthUidStart AND :monthUidEnd")
    List<Fine> getListOfUserData(UUID userId, int monthUidStart, int monthUidEnd);

    @Override
    Optional<Fine> findByUserIdAndDate(UUID userId, LocalDate date);

    @Override
    List<Fine> getDataByUserIdAndChangedAtAfter(UUID userId, Instant changedAt);
}
