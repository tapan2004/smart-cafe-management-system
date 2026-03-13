package com.cafe.api.controller;

import com.cafe.api.dto.response.DashboardResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/dashboard")
public interface DashboardController {
    @GetMapping("/summary")
    ResponseEntity<DashboardResponse> getDashboardDetails();
}