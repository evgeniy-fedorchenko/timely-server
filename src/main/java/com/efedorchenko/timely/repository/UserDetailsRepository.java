package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.Role;
import com.efedorchenko.timely.entity.UserDetailsImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

import static com.efedorchenko.timely.configuration.ApplicationProperties.ROLES_BY_USER_ID_CACHE_NAME;

public interface UserDetailsRepository extends JpaRepository<UserDetailsImpl, UUID> {

    Optional<UserDetails> findByUsername(String username);

    @Cacheable(cacheNames = ROLES_BY_USER_ID_CACHE_NAME, unless = "#result == null", key = "#userId")
    @Query("SELECT u.role FROM UserDetailsImpl u WHERE u.id = :userId")
    Optional<Role> findRoleById(UUID userId);
}
