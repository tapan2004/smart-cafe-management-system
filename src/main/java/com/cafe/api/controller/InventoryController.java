package com.cafe.api.controller;

import com.cafe.api.entity.inventory.InventoryItem;
import com.cafe.api.entity.inventory.ProductIngredient;
import com.cafe.api.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/get")
    public ResponseEntity<List<InventoryItem>> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addInventory(@RequestBody Map<String, String> requestMap) {
        return inventoryService.addInventory(requestMap);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<String> updateInventory(@RequestBody Map<String, String> requestMap) {
        return inventoryService.updateInventory(requestMap);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteInventory(@PathVariable Integer id) {
        return inventoryService.deleteInventory(id);
    }

    @GetMapping("/ingredients/{productId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<List<ProductIngredient>> getIngredientsByProductId(@PathVariable Integer productId) {
        return inventoryService.getIngredientsByProductId(productId);
    }

    @PostMapping("/addIngredient")
    public ResponseEntity<String> addIngredientToProduct(@RequestBody Map<String, String> requestMap) {
        return inventoryService.addIngredientToProduct(requestMap);
    }

    @DeleteMapping("/removeIngredient/{id}")
    public ResponseEntity<String> removeIngredientFromProduct(@PathVariable Integer id) {
        return inventoryService.removeIngredientFromProduct(id);
    }
}
