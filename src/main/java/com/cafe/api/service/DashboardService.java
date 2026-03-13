package com.cafe.api.service;

import com.cafe.api.dto.response.DashboardResponse;
import org.springframework.http.ResponseEntity;

public interface DashboardService {
    ResponseEntity<DashboardResponse> getDashboardDetails();
}