package com.cafe.api.service;

import com.cafe.api.entity.inventory.InventoryItem;
import com.cafe.api.entity.inventory.ProductIngredient;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface InventoryService {
    ResponseEntity<List<InventoryItem>> getAllInventory();
    ResponseEntity<String> addInventory(Map<String, String> requestMap);
    ResponseEntity<String> updateInventory(Map<String, String> requestMap);
    ResponseEntity<String> deleteInventory(Integer id);
    
    ResponseEntity<List<ProductIngredient>> getIngredientsByProductId(Integer productId);
    ResponseEntity<String> addIngredientToProduct(Map<String, String> requestMap);
    ResponseEntity<String> removeIngredientFromProduct(Integer id);
}
