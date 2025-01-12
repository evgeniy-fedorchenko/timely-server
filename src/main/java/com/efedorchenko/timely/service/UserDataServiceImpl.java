package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.UserDataEntity;
import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.exception.ExceptionTemplates;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.mapper.UserDataMapper;
import com.efedorchenko.timely.model.data.DataRangeRequest;
import com.efedorchenko.timely.model.data.UserDataDto;
import com.efedorchenko.timely.model.data.UserDataModifyDto;
import com.efedorchenko.timely.model.data.UserDataType;
import com.efedorchenko.timely.repository.UserDataRepository;
import com.efedorchenko.timely.repository.UserDataRepositoryFactory;
import com.efedorchenko.timely.repository.UserDetailsRepository;
import com.efedorchenko.timely.repository.UserEntityRepository;
import com.efedorchenko.timely.security.model.RoleType;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Log
@Service
@RequiredArgsConstructor
public class UserDataServiceImpl implements UserDataService<UserDataDto, DataRangeRequest> {

    private final ExecutorService executorOfVirtual;
    private final UserDataMapper userDataMapper;
    private final UserEntityRepository userEntityRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final UserDataRepositoryFactory repositoryFactory;

    /**
     * Добавить объект {@link UserDataDto} какому-то юзеру, не тому кто авторизован в данный момент
     * <p>
     * Юзер, которому нужно добавить объект (вернее его {@code UUID userId}) берется из
     * {@link UserDataDto#getToUserId()}. Перед тем как передать данные в метод добавления
     * нового объекта к юзеру, выполняется проверка прав у инициатора - юзера, который
     * авторизован в данный момент и который пытается кому-то добавить новый объект данных.
     * <lu>
     * <li>Если авторизованный юзер имеет роль {@link RoleType#MODERATOR} - ему разрешается добавить данные
     * юзерам без ограничений</li>
     * <li>Если авторизованный юзер имеет роль {@link RoleType#BOSS} или {@link RoleType#CREATOR}, то ему
     * разрешается добавлять данные только к юзерам из своего пространства</li>
     * <li>Юзерам с ролью {@link RoleType#WORKER} не разрешается выполнять этот метод. Такой юзер может
     * добавлять данные только себе </li>
     * </lu>
     *
     * @param initiatorIdOfAdding авторизованный в данный момент юзер, который
     *                            инициирует добавление каких-то данных какому-то юзеру
     * @param dataDto             данные объекта, которые нужно добавить какому-то юзеру.
     *                            Кому именно - берется из {@code userDataDto.getToUserId()}
     */
    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('BOSS', 'CREATOR', 'MODERATOR')")
    public UserDataDto addDataToOtherUser(UUID initiatorIdOfAdding, UserDataDto dataDto) {
        UUID toUserId = dataDto.getToUserId();
        if (toUserId == null) {
            throw ExceptionTemplates.BNS_VAR1.get();
        }
        if (!this.haveAccessToSpaceOf(initiatorIdOfAdding, toUserId)) {
            throw ExceptionTemplates.BNS_VAR5.apply(initiatorIdOfAdding, toUserId);
        }
        return this.addData(toUserId, dataDto);
    }

    @Override
    @Transactional
    public UserDataDto addData(UUID userId, UserDataDto dataDto) {
        UserEntity userEntity = userEntityRepository.findById(userId).orElseThrow();
        UserDataRepository<UserDataEntity> repository = repositoryFactory.getRepository(dataDto.getType());

        if (dataDto.getType().isRepeatable()) {
//            Если такой объект уже существует (по совпадению даты) - просто возвращаем его
            Optional<UserDataEntity> existingData = repository.findByUserIdAndDate(userId, dataDto.getDate());
            if (existingData.isPresent()) {
                return userDataMapper.map(existingData.get());
            }
        }
        UserDataEntity userDataEntity = userDataMapper.map(dataDto, userEntity);
        UserDataEntity savedDataEntity = repository.save(userDataEntity);
        return userDataMapper.map(savedDataEntity);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('BOSS', 'CREATOR', 'MODERATOR')")
    public void deleteData(UUID userId, UUID targetUserId, UserDataType dataType, Long dataId) {
        if (!this.haveAccessToSpaceOf(userId, targetUserId)) {
            throw ExceptionTemplates.BNS_VAR5.apply(userId, targetUserId);
        }

        CompletableFuture.runAsync(() -> {
            UserDataRepository<UserDataEntity> repository = repositoryFactory.getRepository(dataType);
            repository.findById(dataId).ifPresentOrElse(data -> {
                if (!userId.equals(data.getUser().getId())) {
                    throw ExceptionTemplates.BNS_VAR6.apply(dataId, userId);
                }
                data.setDeletedAt(Instant.now());
                repository.save(data);

//                Без исключений, потому что данные, которые нужно удалить, итак не существуют
//                Клиент просто должен обновить данные
            }, () -> log.warn("Data of userID [{}] not found for mark deleted", userId));

        }, executorOfVirtual);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDataDto> getRange(DataRangeRequest dataRangeRequest, UserDataType dataType) {
        int startMonthUid = Helper.getMonthUid(dataRangeRequest.getStartInclusive());
        int endMonthUid = Helper.getMonthUid(dataRangeRequest.getEndInclusive());
        UUID userId = dataRangeRequest.getRequestedUserId();

        UserDataRepository<UserDataEntity> repository = repositoryFactory.getRepository(dataType);
        List<UserDataEntity> entities = repository.findByUserIdAndMonthUidBetween(userId, startMonthUid, endMonthUid);
        return userDataMapper.map(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDataDto> getUpdates(UUID userId, UserDataType dataType, @Nullable Instant since) {
        UserDataRepository<UserDataEntity> repository = repositoryFactory.getRepository(dataType);
        if (since == null) {
            since = Instant.EPOCH;
        }
        List<UserDataEntity> foundEntities = repository.getDataByUserIdAndChangedAtAfter(userId, since);
        return userDataMapper.map(foundEntities);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyAuthority('BOSS', 'CREATOR', 'MODERATOR')")
    public void changeData(UUID userId, UserDataModifyDto modifyingData) {
        UserDataDto newData = modifyingData.getNewData();
        Long dataId = newData.getId(); // FIXME: 03.01.2025 проверить, может в случае отсутствия не бросать исключение а просто сохранять объект как новый
        if (dataId == null) {
            throw ExceptionTemplates.BNS_VAR7.apply(modifyingData);
        }
        UserDataRepository<UserDataEntity> repository = repositoryFactory.getRepository(newData.getType());
        UserDataEntity dataEntity = repository.findById(dataId)
                .orElseThrow(() -> ExceptionTemplates.BNS_VAR8.apply(modifyingData));

        if (!modifyingData.getModifyingUserId().equals(dataEntity.getUser().getId())) {
            throw ExceptionTemplates.BNS_VAR9.apply(dataEntity, modifyingData.getModifyingUserId());
        }

        CompletableFuture.runAsync(() -> {
            UserDataEntity updatedDataEntity = userDataMapper.update(dataEntity, newData);
            repository.save(updatedDataEntity);
        }, executorOfVirtual);
    }

    private boolean haveAccessToSpaceOf(UUID initiatorId, UUID userIdToCompareSpace) {
        RoleType initiatorRole = userDetailsRepository.findRoleById(initiatorId)
                .orElseThrow(() -> ExceptionTemplates.SVR_VAR2.apply(initiatorId))
                .getRoleType();

        if (initiatorRole == RoleType.MODERATOR) {
            return true;
        }
        Long initiatorSpaceId = userEntityRepository.findSpaceIdWhereConsist(initiatorId)
                .orElseThrow(() -> ExceptionTemplates.SVR_VAR3.apply(initiatorId));
        Long addableUserSpaceId = userEntityRepository.findSpaceIdWhereConsist(userIdToCompareSpace)
                .orElseThrow(() -> ExceptionTemplates.BNS_VAR4.apply(userIdToCompareSpace, initiatorId));

        return initiatorSpaceId.equals(addableUserSpaceId);
    }
}
