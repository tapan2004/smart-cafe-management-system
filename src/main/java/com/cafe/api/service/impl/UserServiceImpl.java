package com.cafe.api.service.impl;

import com.cafe.api.dto.request.UserRequestDTO;
import com.cafe.api.dto.response.UserResponseDTO;
import com.cafe.api.entity.roles.Role;
import com.cafe.api.entity.roles.RoleName;
import com.cafe.api.entity.users.User;
import com.cafe.api.repository.RoleRepository;
import com.cafe.api.repository.UserRepository;
import com.cafe.api.security.JwtFilter;
import com.cafe.api.security.JwtUtils;
import com.cafe.api.service.EmailService;
import com.cafe.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;

    @Override
    public ResponseEntity<String> signUp(UserRequestDTO request) {
        Optional<User> email = userRepository.findByEmail(request.getEmail());

        if (email.isPresent()) {
            return new ResponseEntity<>("Email Already Exists", HttpStatus.BAD_REQUEST);
        }
        Role staffRole = roleRepository
                .findByUserRole(RoleName.ROLE_STAFF)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = new User();
        user.setName(request.getName());
        user.setContactNumber(request.getContactNumber());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(false);
        user.setRoles(Set.of(staffRole));

        User savedUser = userRepository.save(user);
        emailService.sendSimpleMessage(
                savedUser.getEmail(),
                "Registration Successful",
                "Hello " + savedUser.getName() +
                        ",\n\nYour account has been created successfully.\nPlease wait for admin approval."
        );
        return ResponseEntity.ok("User Registered Successfully");
    }

    @Override
    public ResponseEntity<String> login(UserRequestDTO request) {
        Optional<User> optional = userRepository.findByEmail(request.getEmail());

        if (optional.isEmpty()) {
            return new ResponseEntity<>(
                    "Bad Credentials", HttpStatus.BAD_REQUEST);
        }

        User user = optional.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new ResponseEntity<>(
                    "Bad Credentials", HttpStatus.BAD_REQUEST);
        }
        if (!user.getStatus()) {
            return new ResponseEntity<>(
                    "Wait for admin approval", HttpStatus.BAD_REQUEST);
        }
        String token = jwtUtils.generateToken(user.getEmail());
        return ResponseEntity.ok(token);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userRepository.getAllUsers());
    }

    @Override
    public ResponseEntity<String> updateUserStatus(UserRequestDTO request) {
        Optional<User> optional = userRepository.findById(request.getId());

        if (optional.isEmpty()) {
            return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
        }
        User user = optional.get();
        user.setStatus(request.getStatus());
        userRepository.save(user);
        return ResponseEntity.ok("User Status Updated");
    }
}