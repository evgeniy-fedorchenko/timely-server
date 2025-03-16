package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.Space;
import com.efedorchenko.timely.entity.SpaceStatus;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.mapper.SpaceMapper;
import com.efedorchenko.timely.model.AcceptMember;
import com.efedorchenko.timely.model.MemberOpResult;
import com.efedorchenko.timely.model.MembersResponse;
import com.efedorchenko.timely.model.SpaceConnectResponse;
import com.efedorchenko.timely.model.SpaceDto;
import com.efedorchenko.timely.model.SpaceKeys;
import com.efedorchenko.timely.model.SpaceMember;
import com.efedorchenko.timely.repository.SpaceRepository;
import com.efedorchenko.timely.repository.UserEntityRepository;
import com.efedorchenko.timely.security.UserDetailsServiceImpl;
import com.efedorchenko.timely.security.model.RoleType;
import lombok.AllArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.EnumSet;
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
    private final SpaceMapper spaceMapper;
    private final UserDetailsServiceImpl userDetailsService;
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
    public MembersResponse getMembers(UUID userId, @Nullable Instant since, boolean withJoinRequests) {
        Optional<Space> spaceIdOpt = userEntityRepository.findSpaceWhereConsist(userId);
        if (spaceIdOpt.isEmpty()) {
            SpaceStatus spaceStatus = userEntityRepository.findSpaceStatusByUserId(userId).orElse(SpaceStatus.NONE);
            return MembersResponse.emptyWith(spaceStatus);
        }
        Space space = spaceIdOpt.get();
        CompletableFuture<List<UUID>> actualIds = CompletableFuture.supplyAsync(
                () -> userEntityRepository.findIdsIdByConsistsInSpace(space.getId()),
                executorOfVirtual
        );

        Instant _since = since == null ? Instant.EPOCH : since;
        EnumSet<SpaceStatus> searchStatuses = EnumSet.of(SpaceStatus.MEMBER);
        if (withJoinRequests) {
            searchStatuses.add(SpaceStatus.PENDING_BOSS);
            searchStatuses.add(SpaceStatus.PENDING_WORKER);
        }
        List<SpaceMember> members = userEntityRepository.findMembers(space.getId(), _since, searchStatuses);

        SpaceDto spaceDto = spaceMapper.map(space);
        return MembersResponse.of(members, spaceDto, actualIds.join());
    }

    /**
     * Покинуть пространство, в котором состоит юзер
     *
     * @param userId, который покидает пространство
     */
    @Override
    @Transactional
    public boolean leaveSpace(UUID userId) {
        return disconnectFromOurSpace(userId);
    }

    /**
     * Выгнать другого юзера из своего пространства
     *
     * @param initiatorUserId юзер, который инициирует операцию изгнания
     * @param kickedUserId    юзер, которого выгоняют
     */
    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('BOSS', 'CREATOR', 'MODERATOR')")
    public boolean detachUser(UUID initiatorUserId, UUID kickedUserId) {
        userDetailsService.checkAccessToSpaceOf(initiatorUserId, kickedUserId);
        return disconnectFromOurSpace(kickedUserId);
    }

    /**
     * Подать заявку на вступление в пространство.
     *
     * @param userId   кто подает заявку
     * @param spaceKey ключ для поиска пространства
     */
    @Override
    @Transactional
    public SpaceConnectResponse requestConnectToSpace(UUID userId, String spaceKey) {
        return spaceRepository.findByWorkerKey(spaceKey)
                .map(space -> requestConnectToSpace(userId, RoleType.WORKER, space))
                .orElseGet(() -> spaceRepository.findByBossKey(spaceKey)
                        .map(space -> requestConnectToSpace(userId, RoleType.BOSS, space))
                        .orElse(SpaceConnectResponse.keyInvalid())
                );
    }

    /**
     * Принять заявку юзера на вступление в пространство
     *
     * @param userId       userId, который принимает заявку
     * @param acceptMember настройки юзера, которого принимают
     */
    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('BOSS', 'CREATOR', 'MODERATOR')")
    public MemberOpResult acceptMember(UUID userId, AcceptMember acceptMember) {
        UUID acceptedUserId = acceptMember.getAcceptedUserId();
        userDetailsService.checkAccessToSpaceOf(userId, acceptedUserId);

        return switch (userEntityRepository.findSpaceStatusByUserId(acceptedUserId).orElseThrow()) {
            case PENDING_WORKER, PENDING_BOSS -> {
                userEntityRepository.setStatus(acceptedUserId, SpaceStatus.MEMBER.name());
                userDetailsService.addRole(acceptMember.getNewRole(), acceptedUserId);
                yield MemberOpResult.success();
            }
            case NONE -> MemberOpResult.alreadyCanceled();
            case MEMBER -> MemberOpResult.alreadyProcessed();
        };
    }

    private boolean disconnectFromOurSpace(UUID targetUserId) {
        Optional<UserEntity> userEntityOpt = userEntityRepository.findById(targetUserId);
        boolean isUserPresent = userEntityOpt.isPresent();
        if (isUserPresent) {
            UserEntity user = userEntityOpt.get();
            CompletableFuture.runAsync(() -> {
                user.setConsistsInSpace(null);
                user.setSpaceStatus(SpaceStatus.NONE);
                userDetailsService.addRole(RoleType.WORKER, targetUserId);
                userEntityRepository.save(user);
            }, executorOfVirtual);
        }
        return isUserPresent;
    }

    private SpaceConnectResponse requestConnectToSpace(UUID userId, RoleType newRole, Space space) {
        SpaceStatus spaceStatus = newRole.getPreAcceptSpaceStatus();
        CompletableFuture.runAsync(() -> {
            UserEntity userEntity = userEntityRepository.findById(userId).orElseThrow();
            userEntity.setConsistsInSpace(space);
            userEntity.setSpaceStatus(spaceStatus);
            userDetailsService.addRole(newRole, userId);
            userEntityRepository.save(userEntity);
        }, executorOfVirtual);

        return SpaceConnectResponse.success(spaceStatus);
    }
}
