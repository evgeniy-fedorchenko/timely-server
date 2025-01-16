package com.efedorchenko.timely.security;

import com.efedorchenko.timely.entity.Role;
import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.exception.ExceptionTemplates;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.repository.RoleRepository;
import com.efedorchenko.timely.repository.UserDetailsRepository;
import com.efedorchenko.timely.security.model.RoleType;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.efedorchenko.timely.configuration.ApplicationProperties.ROLES_BY_USER_ID_CACHE_NAME;

@Log
@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final RoleRepository roleRepository;
    private final UserDetailsRepository userDetailsRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDetailsRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by username: " + username));
    }

    @Transactional
    @CacheEvict(cacheNames = ROLES_BY_USER_ID_CACHE_NAME, key = "#userId")
    public void addRole(RoleType roleType, UUID userId) {
        UserDetailsImpl userDetails = userDetailsRepository.findById(userId)
                .orElseThrow(() -> ExceptionTemplates.SVR_VAR13.apply(userId, roleType));

        Role role = roleRepository.findByRoleType(roleType)
                .orElseThrow(() -> ExceptionTemplates.SVR_VAR14.apply(roleType));

        userDetails.setRole(role);
        userDetailsRepository.save(userDetails);
    }
}
