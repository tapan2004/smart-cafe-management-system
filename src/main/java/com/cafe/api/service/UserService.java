package com.cafe.api.service;

import com.cafe.api.dto.request.UserRequestDTO;
import com.cafe.api.dto.response.UserResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UserService {
    ResponseEntity<String> signUp(UserRequestDTO request);

    ResponseEntity<String> login(UserRequestDTO request);

    ResponseEntity<List<UserResponseDTO>> getAllUsers();

    ResponseEntity<String> updateUserStatus(UserRequestDTO request);
}