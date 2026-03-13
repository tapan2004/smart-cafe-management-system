package com.cafe.api.service.impl;

import com.cafe.api.dto.response.DashboardResponse;
import com.cafe.api.repository.BillRepository;
import com.cafe.api.repository.CategoryRepository;
import com.cafe.api.repository.DashboardRepository;
import com.cafe.api.repository.ProductRepository;
import com.cafe.api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final DashboardRepository dashboardRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BillRepository billRepository;

    @Override
    public ResponseEntity<DashboardResponse> getDashboardDetails() {

        DashboardResponse response = new DashboardResponse();

        // Basic counts
        response.setTotalCategories(categoryRepository.count());
        response.setTotalProducts(productRepository.count());
        response.setTotalBills(billRepository.count());

        // TOTAL Revenue
        Double revenue = dashboardRepository.getTotalRevenue();
        response.setTotalRevenue(revenue == null ? 0 : revenue);

        // DASHBOARD DATA
        response.setTopProducts(dashboardRepository.getTopSellingProducts());
        response.setMonthlyRevenue(dashboardRepository.getMonthlyRevenue());
        response.setRecentOrders(dashboardRepository.getRecentOrders());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}