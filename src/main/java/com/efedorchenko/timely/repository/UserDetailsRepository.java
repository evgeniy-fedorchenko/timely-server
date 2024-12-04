package com.efedorchenko.timely.repository;

import com.efedorchenko.timely.entity.Role;
import com.efedorchenko.timely.entity.UserDetailsImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

public interface UserDetailsRepository extends JpaRepository<UserDetailsImpl, UUID> {

    Optional<UserDetails> findByUsername(String username);

    @Query("SELECT u.role FROM UserDetailsImpl u WHERE u.id = :userId")
    Optional<Role> findRoleById(UUID userId);
}
