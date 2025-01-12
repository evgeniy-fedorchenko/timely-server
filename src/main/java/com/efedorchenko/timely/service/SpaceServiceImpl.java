package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.mapper.SpaceMapper;
import com.efedorchenko.timely.mapper.UserMapper;
import com.efedorchenko.timely.model.MembersResult;
import com.efedorchenko.timely.model.SpaceCreateDto;
import com.efedorchenko.timely.model.SpaceKeys;
import com.efedorchenko.timely.model.SpaceMember;
import com.efedorchenko.timely.repository.SpaceRepository;
import com.efedorchenko.timely.repository.UserEntityRepository;
import com.efedorchenko.timely.security.model.RoleType;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
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
    private final SpaceRepository spaceRepository;
    private final UserEntityRepository userEntityRepository;

    private final Function<String, String> keyGenerator = prefix ->
            prefix + UUID.randomUUID().toString().substring(KEY_LEN_FACTOR).replaceAll("-", "");

    /**
     * Поиск юзера по {@code userId}, создание пространства,
     * добавление роли {@link RoleType#CREATOR} к {@link UserDataServiceImpl}
     * и добавление свежесозданного пространства к найденному юзеру
     */
    @Override
    @Transactional
    public SpaceKeys create(UUID userId, SpaceCreateDto spaceCreateDto) {
        return create(userId, spaceCreateDto, createDetachedKeys());
    }

    @Override
    @Transactional
    public SpaceKeys create(UUID userId, SpaceCreateDto spaceCreateDto, SpaceKeys spaceKeys) {

        CompletableFuture.runAsync(() -> {
            UserEntity creator = userEntityRepository.findById(userId).orElseThrow();
            Space space = spaceMapper.map(spaceCreateDto, spaceKeys, creator);
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
    public MembersResult getMembers(UUID userId, @jakarta.annotation.Nullable Instant since) {
        boolean consistInSpace = userEntityRepository.findById(userId).map(UserEntity::getConsistsInSpace).isPresent();
        if (!consistInSpace) {
            return MembersResult.notConsist();
        }
        Instant _since = since == null ? Instant.EPOCH : since;
        List<SpaceMember> members = userEntityRepository.findSpaceIdWhereConsist(userId)
                .map(spaceId -> userEntityRepository.findByConsistsInSpaceIdAndChangedAtAfter(spaceId, _since))
                .stream()
                .flatMap(List::stream)
                .map(userMapper::map)
                .toList();

        return MembersResult.withMembers(members);
    }

    @Override
    @Transactional
    public boolean leaveSpace(UUID userId) {
        return false;
    }

    @Override
    @Transactional
    public boolean detachUser(UUID userId, UUID targetUserId) {
        return false;
    }
}
