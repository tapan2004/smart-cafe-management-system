package com.cafe.api.controller;

import com.cafe.api.dto.request.UserRequestDTO;
import com.cafe.api.dto.response.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/user")
public interface UserController {

    @PostMapping("/signup")
    ResponseEntity<String> signUp(@RequestBody UserRequestDTO request);

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody UserRequestDTO request);

    @GetMapping("/all")
    ResponseEntity<List<UserResponseDTO>> getAllUsers();

    @PutMapping("/updateStatus")
    ResponseEntity<String> updateUserStatus(@RequestBody UserRequestDTO request);
}