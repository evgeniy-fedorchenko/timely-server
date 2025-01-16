package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {

    @Query("SELECT u.consistsInSpace.id FROM UserEntity u WHERE u.id = :userId ")
    Optional<Long> findSpaceIdWhereConsist(UUID userId);

    @Query("SELECT u.id FROM UserEntity u WHERE u.consistsInSpace.id = :spaceId")
    List<UUID> findAllIdByConsistsInSpaceId(Long spaceId);

    List<UserEntity> findByConsistsInSpaceIdAndChangedAtAfter(Long spaceId, Instant changedAt);
}
