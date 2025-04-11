package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.Role;
import com.efedorchenko.timely.security.model.RoleType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static com.efedorchenko.timely.configuration.properties.ApplicationProperties.ROLES_CACHE_NAME;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Cacheable(cacheNames = ROLES_CACHE_NAME, unless = "#result == null", key = "#value")
    Optional<Role> findByRoleType(RoleType value);

    boolean existsByRoleType(RoleType roleType);
}
