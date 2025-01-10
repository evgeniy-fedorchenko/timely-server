package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.configuration.ApplicationProperties;
import com.efedorchenko.timely.entity.Role;
import com.efedorchenko.timely.security.model.RoleType;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Cacheable(cacheNames = ApplicationProperties.ROLES_CACHE_NAME, unless = "#result == null")
    Optional<Role> findByRoleType(RoleType value);

    @Query("SELECT r.roleType FROM Role r")
    Set<RoleType> getRoleTypes();

    boolean existsByRoleType(RoleType roleType);
}
