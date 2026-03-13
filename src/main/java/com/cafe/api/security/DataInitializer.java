package com.cafe.api.security;

import com.cafe.api.entity.roles.Role;
import com.cafe.api.entity.roles.RoleName;
import com.cafe.api.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String @NonNull ... args) {
        for (RoleName role : RoleName.values()) {

            roleRepository.findByUserRole(role)
                    .orElseGet(() ->
                            roleRepository.save(new Role(null, role))
                    );
        }
    }
}