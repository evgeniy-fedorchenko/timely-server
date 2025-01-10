package com.efedorchenko.timely.configuration;

import com.efedorchenko.timely.entity.Role;
import com.efedorchenko.timely.repository.RoleRepository;
import com.efedorchenko.timely.security.model.RoleType;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@AllArgsConstructor
public class RolesLoader implements ApplicationRunner {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Arrays.stream(RoleType.values())
                .forEach(roleType -> {
                    if (!roleRepository.existsByRoleType(roleType)) {
                        Role role = Role.fromRoleType(roleType);
                        roleRepository.save(role);
                    }
                });
    }
}
