package com.efedorchenko.timely.service;

import com.efedorchenko.timely.mapper.UserMapper;
import com.efedorchenko.timely.model.SpaceMember;
import com.efedorchenko.timely.repository.UserEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final UserMapper userMapper;
    private final UserEntityRepository userEntityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SpaceMember> getMembers(UUID userId) {
        return userEntityRepository.findSpaceIdWhereConsist(userId)
                .map(userEntityRepository::findByConsistsInSpaceId)
                .stream()
                .flatMap(List::stream)
                .map(userMapper::map)
                .toList();
    }
}
