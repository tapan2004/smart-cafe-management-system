package com.cafe.api.repository;

import com.cafe.api.entity.bill.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Integer> {

    Optional<Bill> findByUuid(String uuid);

    List<Bill> findByStatusNot(com.cafe.api.entity.bill.OrderStatus status);

    @Query("""
            SELECT b FROM Bill b
            LEFT JOIN FETCH b.items i
            LEFT JOIN FETCH i.product p
            LEFT JOIN FETCH p.category
            WHERE b.uuid = :uuid
            """)
    Optional<Bill> findBillWithItems(@Param("uuid") String uuid);

    @Query("select b from Bill b order by b.id desc")
    List<Bill> getAllBills();

    @Query("select b from Bill b where b.createdBy=:username " +
            "order by b.id desc")
    List<Bill> getBillByUserName(@Param("username") String currentUser);

    @Query("SELECT HOUR(b.createdAt) as hour, COUNT(b.id) as count FROM Bill b GROUP BY HOUR(b.createdAt) ORDER BY COUNT(b.id) DESC")
    List<Object[]> getPeakHoursData();

    @Query("SELECT DATE(b.createdAt) as date, SUM(b.total) as total FROM Bill b GROUP BY DATE(b.createdAt) ORDER BY DATE(b.createdAt) ASC")
    List<Object[]> getDailyRevenueTrend();
}