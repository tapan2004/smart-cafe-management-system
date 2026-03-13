package com.cafe.api.controller.imp;

import com.cafe.api.controller.UserController;
import com.cafe.api.dto.request.UserRequestDTO;
import com.cafe.api.dto.response.UserResponseDTO;
import com.cafe.api.security.JwtUtils;
import com.cafe.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserControllerImp implements UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Override
    public ResponseEntity<String> signUp(UserRequestDTO request) {
        return userService.signUp(request);
    }

    @Override
    public ResponseEntity<String> login(UserRequestDTO request) {
        return userService.login(request);
    }

    @Override
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return userService.getAllUsers();
    }

    @Override
    public ResponseEntity<String> updateUserStatus(UserRequestDTO request) {
        return userService.updateUserStatus(request);
    }
}