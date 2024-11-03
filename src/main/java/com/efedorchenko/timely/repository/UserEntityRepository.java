package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.UUID;

public interface UserEntityRepository extends R2dbcRepository<UserEntity, UUID> {
}
