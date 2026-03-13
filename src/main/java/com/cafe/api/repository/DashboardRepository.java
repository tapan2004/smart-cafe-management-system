package com.cafe.api.repository;

import com.cafe.api.dto.response.dashboard.MonthlyRevenueDTO;
import com.cafe.api.dto.response.dashboard.RecentOrderDTO;
import com.cafe.api.dto.response.dashboard.TopProductDTO;
import com.cafe.api.entity.bill.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DashboardRepository extends JpaRepository<Bill, Integer> {
    // TOP SELLING PRODUCTS
    @Query("""
                SELECT new com.cafe.api.dto.response.dashboard.TopProductDTO(
                    p.name,
                    SUM(b.quantity)
                )
                FROM BillItem b
                JOIN b.product p
                GROUP BY p.name
                ORDER BY SUM(b.quantity) DESC
            """)
    List<TopProductDTO> getTopSellingProducts();

    // MONTHLY REVENUE
    @Query("""
                SELECT new com.cafe.api.dto.response.dashboard.MonthlyRevenueDTO(
                    MONTH(b.createdAt),
                    SUM(b.total)
                )
                FROM Bill b
                GROUP BY MONTH(b.createdAt)
                ORDER BY MONTH(b.createdAt)
            """)
    List<MonthlyRevenueDTO> getMonthlyRevenue();

    // RECENT ORDERS
    @Query("""
                SELECT new com.cafe.api.dto.response.dashboard.RecentOrderDTO(
                    b.uuid,
                    b.name,
                    b.total,
                    b.createdBy
                )
                FROM Bill b
                ORDER BY b.id DESC
            """)
    List<RecentOrderDTO> getRecentOrders();

    // TOTAL REVENUE
    @Query("SELECT SUM(b.total) FROM Bill b")
    Double getTotalRevenue();
}