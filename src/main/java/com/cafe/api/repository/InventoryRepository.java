package com.cafe.api.repository;

import com.cafe.api.entity.inventory.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Integer> {
    
    // Custom query to find items below threshold
    List<InventoryItem> findByQuantityLessThanEqual(Double threshold);
    
    // Or just compare quantity with lowStockThreshold field
}
