package com.efedorchenko.timely.service;

import com.efedorchenko.timely.entity.UserEntity;
import com.efedorchenko.timely.mapper.UserMapper;
import com.efedorchenko.timely.model.MembersResult;
import com.efedorchenko.timely.model.SpaceMember;
import com.efedorchenko.timely.repository.SpaceRepository;
import com.efedorchenko.timely.repository.UserEntityRepository;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final UserMapper userMapper;
    private final UserEntityRepository userEntityRepository;
    private final SpaceRepository spaceRepository;

    @Override
    @Transactional(readOnly = true)
    public MembersResult getMembers(UUID userId, @Nullable Instant since) {
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
