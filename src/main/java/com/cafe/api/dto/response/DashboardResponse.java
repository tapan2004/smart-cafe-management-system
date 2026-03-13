package com.cafe.api.dto.response;

import com.cafe.api.dto.response.dashboard.MonthlyRevenueDTO;
import com.cafe.api.dto.response.dashboard.RecentOrderDTO;
import com.cafe.api.dto.response.dashboard.TopProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private Long totalCategories;
    private Long totalProducts;
    private Long totalBills;
    private Double totalRevenue;

    private List<TopProductDTO> topProducts;
    private List<MonthlyRevenueDTO> monthlyRevenue;
    private List<RecentOrderDTO> recentOrders;
}