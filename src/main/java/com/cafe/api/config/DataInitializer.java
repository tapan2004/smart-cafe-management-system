package com.cafe.api.config;

import com.cafe.api.entity.roles.Role;
import com.cafe.api.entity.roles.RoleName;
import com.cafe.api.entity.users.User;
import com.cafe.api.repository.RoleRepository;
import com.cafe.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");

        // Ensure roles exist
        Role adminRole = ensureRoleExists(RoleName.ROLE_ADMIN);
        ensureRoleExists(RoleName.ROLE_STAFF);
        ensureRoleExists(RoleName.ROLE_MANAGER);

        // Ensure multiple admin users exist and are correct for testing
        String[] adminEmails = {"admin@cafe.com", "john@example.com"};
        for (String email : adminEmails) {
            userRepository.findByEmail(email).ifPresentOrElse(
                user -> {
                    log.info("Updating existing user: {}", email);
                    user.setPassword(passwordEncoder.encode("admin123"));
                    user.setStatus(true);
                    Set<Role> roles = new HashSet<>(user.getRoles());
                    roles.add(adminRole);
                    user.setRoles(roles);
                    userRepository.save(user);
                },
                () -> {
                    log.info("Creating new admin user: {}", email);
                    User user = new User();
                    user.setName("Admin User");
                    user.setEmail(email);
                    user.setPassword(passwordEncoder.encode("admin123"));
                    user.setContactNumber("9876543210");
                    user.setStatus(true);
                    user.setRoles(Set.of(adminRole));
                    userRepository.save(user);
                }
            );
        }

        log.info("Data initialization completed.");
    }

    private Role ensureRoleExists(RoleName roleName) {
        return roleRepository.findByUserRole(roleName).orElseGet(() -> {
            log.info("Creating missing role: {}", roleName);
            Role role = new Role();
            role.setUserRole(roleName);
            return roleRepository.save(role);
        });
    }
}
