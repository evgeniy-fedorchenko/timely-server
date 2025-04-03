package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.entity.SpaceStatus;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.model.SpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {

    @Query("SELECT u.consistsInSpace FROM UserEntity u WHERE u.id = :userId ")
    Optional<Space> findSpaceWhereConsist(UUID userId);

    @Query("SELECT u.consistsInSpace.id FROM UserEntity u WHERE u.id = :userId ")
    Optional<Long> findSpaceIdWhereConsist(UUID userId);

    @Query("SELECT u.id FROM UserEntity u WHERE u.consistsInSpace.id = :spaceId")
    List<UUID> findIdsIdByConsistsInSpace(Long spaceId);

    @Query("""
                SELECT new com.efedorchenko.timely.model.SpaceMember(
                    u.id,
                    u.name,
                    u.position,
                    d.role.roleType,
                    u.rate,
                    u.spaceStatus,
                    u.changedAt
                ) FROM UserEntity u
                JOIN UserDetailsImpl d ON d.id = u.id
                WHERE u.consistsInSpace.id = :spaceId
                AND u.changedAt > :changedAt
                AND u.spaceStatus IN :statuses
            """)
    List<SpaceMember> findMembers(Long spaceId, Instant changedAt, Collection<SpaceStatus> statuses);

    @Query("SELECT u.spaceStatus FROM UserEntity u WHERE u.id = :userId")
    Optional<SpaceStatus> findSpaceStatus(UUID userId);

    @Modifying
    @NativeQuery(value = """
            UPDATE users.users
            SET space_status = CAST(:newStatus AS users.space_consist_status)
            WHERE id = :userId
            """)
    void setStatus(UUID userId, String newStatus);
}
