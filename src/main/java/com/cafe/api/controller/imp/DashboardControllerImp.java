package com.cafe.api.controller.imp;

import com.cafe.api.controller.DashboardController;
import com.cafe.api.dto.response.DashboardResponse;
import com.cafe.api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardControllerImp implements DashboardController {
    private final DashboardService dashboardService;

    @Override
    public ResponseEntity<DashboardResponse> getDashboardDetails() {
        return dashboardService.getDashboardDetails();
    }
}