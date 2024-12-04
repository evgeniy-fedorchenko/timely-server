package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.configuration.ApplicationProperties;
import com.efedorchenko.timely.entity.Role;
import com.efedorchenko.timely.model.auth.RoleType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Cacheable(cacheNames = ApplicationProperties.ROLES_CACHE_NAME, unless = "#result == null")
    Optional<Role> findByValue(RoleType value);

}
