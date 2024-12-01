package com.efedorchenko.timely.security;

import com.efedorchenko.timely.entity.Role;
import com.efedorchenko.timely.entity.UserDetailsImpl;
import com.efedorchenko.timely.exception.ServerException;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.model.auth.RoleType;
import com.efedorchenko.timely.repository.RoleRepository;
import com.efedorchenko.timely.repository.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Log
@Component
@RequiredArgsConstructor
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
    public void addRole(RoleType roleType, UUID userId) {
        UserDetailsImpl userDetails = userDetailsRepository.findById(userId).orElseThrow(() -> {
            String roleEntity = roleRepository.findByValue(roleType)
                    .map(Role::toString)
                    .orElse("role entity not found");
            String errMess = "User not found with id [%s] for add role: [%s] (role entity: [%s]), please check why he was authorized"
                    .formatted(userId.toString(), roleType, roleEntity);
            return new ServerException(errMess);
        });

        Role role = roleRepository.findByValue(roleType).orElseThrow(() -> {
            String errMess = "Role [%s] not found for add to user: [%s], available roles only [%s]. Please check how validation allowed this type"
                    .formatted(roleType, userDetails.toString(), roleRepository.getValues());
            return new ServerException(errMess);
        });

        userDetails.addRole(role);
        userDetailsRepository.save(userDetails);
    }
}
