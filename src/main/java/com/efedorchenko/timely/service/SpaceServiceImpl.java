package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.mapper.SpaceMapper;
import com.efedorchenko.timely.mapper.UserMapper;
import com.efedorchenko.timely.model.GetMembersResponse;
import com.efedorchenko.timely.model.SpaceConnectResponse;
import com.efedorchenko.timely.model.SpaceDto;
import com.efedorchenko.timely.model.SpaceKeys;
import com.efedorchenko.timely.model.SpaceMember;
import com.efedorchenko.timely.repository.SpaceRepository;
import com.efedorchenko.timely.repository.UserDetailsRepository;
import com.efedorchenko.timely.repository.UserEntityRepository;
import com.efedorchenko.timely.security.UserDetailsServiceImpl;
import com.efedorchenko.timely.security.model.RoleType;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

@Log
@Service
@AllArgsConstructor
public class SpaceServiceImpl implements SpaceService {

    private static final int KEY_LEN_FACTOR = 19;
    private static final String BOSS_PREFIX = "boss-";
    private static final String WORKER_PREFIX = "worker-";

    private final ExecutorService executorOfVirtual;
    private final UserMapper userMapper;
    private final SpaceMapper spaceMapper;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserDetailsRepository userDetailsRepository;
    private final SpaceRepository spaceRepository;
    private final UserEntityRepository userEntityRepository;

    private final Function<String, String> keyGenerator = prefix ->
            prefix + UUID.randomUUID().toString().substring(KEY_LEN_FACTOR).replaceAll("-", "");

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('CREATOR', 'MODERATOR')")
    public SpaceKeys create(UUID userId, SpaceDto spaceDto, SpaceKeys spaceKeys) {

        CompletableFuture.runAsync(() -> {
            UserEntity creator = userEntityRepository.findById(userId).orElseThrow();
            Space space = spaceMapper.map(spaceDto, spaceKeys, creator);
            spaceRepository.save(space);

//            Создатель пространства состоит в своем же пространстве
            creator.setCreatedSpace(space);
            creator.setConsistsInSpace(space);
            userEntityRepository.save(creator);

        }, executorOfVirtual);

        return spaceKeys;
    }

    @Nullable
    @Override
    @Transactional(readOnly = true)
    public Space findSpace(String spaceKey, RoleType roleType) {
        if (spaceKey == null || roleType == null) {
            return null;
        }
        Optional<Space> spaceOptional = roleType == RoleType.WORKER
                ? spaceRepository.findByWorkerKey(spaceKey)
                : spaceRepository.findByBossKey(spaceKey);
        return spaceOptional.orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public SpaceKeys getKeys(UUID userId) {
        return userEntityRepository.findById(userId)
                .map(UserEntity::getCreatedSpace)
                .map(space -> new SpaceKeys(space.getWorkerKey(), space.getBossKey()))
                .orElse(null);
    }

    @Override
    public SpaceKeys createDetachedKeys() {
        return new SpaceKeys(keyGenerator.apply(WORKER_PREFIX), keyGenerator.apply(BOSS_PREFIX));
    }

    @Override
    @Transactional(readOnly = true)
    public GetMembersResponse getMembers(UUID userId, @Nullable Instant since) {
        Optional<Long> spaceIdOpt = userEntityRepository.findSpaceIdWhereConsist(userId);
        if (spaceIdOpt.isEmpty()) {
            return GetMembersResponse.youNotConsist();
        }
        Long spaceId = spaceIdOpt.get();
        CompletableFuture<List<UUID>> actualIds =
                CompletableFuture.supplyAsync(() -> userEntityRepository.findAllIdByConsistsInSpaceId(spaceId));

        Instant _since = since == null ? Instant.EPOCH : since;
        List<SpaceMember> members = userEntityRepository.findByConsistsInSpaceIdAndChangedAtAfter(spaceId, _since)
                .stream()
                .map(userEntity -> {
                    RoleType roleType = userDetailsRepository.findRoleTypeById(userEntity.getId()).orElseThrow();
                    return userMapper.map(userEntity, roleType);
                })
                .toList();

        return GetMembersResponse.with(members, actualIds.join());
    }

    @Override
    @Transactional
    public boolean leaveSpace(UUID userId) {
        return disconnectFromOurSpace(userId);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('BOSS', 'CREATOR', 'MODERATOR')")
    public boolean detachUser(UUID targetUserId) {
        return disconnectFromOurSpace(targetUserId);
    }

    @Override
    @Transactional
    public SpaceConnectResponse connectToSpace(UUID userId, String spaceKey) {
        return spaceRepository.findByWorkerKey(spaceKey)
                .map(space -> connectUserToSpace(userId, RoleType.WORKER, space))
                .orElseGet(() -> spaceRepository.findByBossKey(spaceKey)
                        .map(space -> connectUserToSpace(userId, RoleType.BOSS, space))
                        .orElse(SpaceConnectResponse.fail())
                );
    }

    private boolean disconnectFromOurSpace(UUID targetUserId) {
        return userEntityRepository.findById(targetUserId)
                .map(user -> {
                    user.setConsistsInSpace(null);
                    userDetailsService.addRole(RoleType.WORKER, targetUserId);
                    return userEntityRepository.save(user);
                }).isPresent();
    }

    private SpaceConnectResponse connectUserToSpace(UUID userId, RoleType newRole, Space space) {
        CompletableFuture.runAsync(() -> {
            UserEntity userEntity = userEntityRepository.findById(userId).orElseThrow();
            userEntity.setConsistsInSpace(space);
            userDetailsService.addRole(newRole, userId);
            userEntityRepository.save(userEntity);
        });
        return SpaceConnectResponse.success(newRole, spaceMapper.map(space));
    }
}
