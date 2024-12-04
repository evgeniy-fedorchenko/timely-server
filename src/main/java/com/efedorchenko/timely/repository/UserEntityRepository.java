package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {

    @Query("SELECT u.consistsInSpace.id FROM UserEntity u WHERE u.id = :userId ")
    Optional<Long> findSpaceIdWhereConsist(UUID userId);
}
