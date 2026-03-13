package com.cafe.api.repository;

import com.cafe.api.entity.bill.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Integer> {

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
}