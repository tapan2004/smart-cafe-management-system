package com.cafe.api.service.impl;

import com.cafe.api.constant.CafeConstants;
import com.cafe.api.entity.inventory.InventoryItem;
import com.cafe.api.entity.inventory.ProductIngredient;
import com.cafe.api.entity.product.Product;
import com.cafe.api.repository.IngredientRepository;
import com.cafe.api.repository.InventoryRepository;
import com.cafe.api.repository.ProductRepository;
import com.cafe.api.security.JwtFilter;
import com.cafe.api.service.InventoryService;
import com.cafe.api.util.CafeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;
    private final JwtFilter jwtFilter;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<List<InventoryItem>> getAllInventory() {
        try {
            return new ResponseEntity<>(inventoryRepository.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting inventory", e);
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<String> addInventory(Map<String, String> requestMap) {
        try {
            if (validateInventoryMap(requestMap, false)) {
                inventoryRepository.save(getInventoryFromMap(requestMap, false));
                return CafeUtils.getResponseEntity("Inventory Added Successfully", HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error adding inventory", e);
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<String> updateInventory(Map<String, String> requestMap) {
        try {
            if (validateInventoryMap(requestMap, true)) {
                Optional<InventoryItem> optional = inventoryRepository.findById(Integer.parseInt(requestMap.get("id")));
                if (optional.isPresent()) {
                    inventoryRepository.save(getInventoryFromMap(requestMap, true));
                    return CafeUtils.getResponseEntity("Inventory Updated Successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Inventory id does not exist", HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error updating inventory", e);
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<String> deleteInventory(Integer id) {
        try {
            Optional<InventoryItem> optional = inventoryRepository.findById(id);
            if (optional.isPresent()) {
                inventoryRepository.deleteById(id);
                return CafeUtils.getResponseEntity("Inventory Deleted Successfully", HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity("Inventory id does not exist", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error deleting inventory", e);
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<List<ProductIngredient>> getIngredientsByProductId(Integer productId) {
        try {
            return new ResponseEntity<>(ingredientRepository.findByProductId(productId), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting ingredients", e);
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<String> addIngredientToProduct(Map<String, String> requestMap) {
        try {
            if (requestMap.containsKey("productId") && requestMap.containsKey("inventoryId") && requestMap.containsKey("quantity")) {
                String productIdStr = requestMap.get("productId");
                String inventoryIdStr = requestMap.get("inventoryId");
                String quantityStr = requestMap.get("quantity");

                if (productIdStr == null || productIdStr.isEmpty() || 
                    inventoryIdStr == null || inventoryIdStr.isEmpty() || 
                    quantityStr == null || quantityStr.isEmpty()) {
                    return CafeUtils.getResponseEntity("Missing required ingredient details", HttpStatus.BAD_REQUEST);
                }

                Optional<Product> productOpt = productRepository.findById(Integer.parseInt(productIdStr));
                Optional<InventoryItem> invOpt = inventoryRepository.findById(Integer.parseInt(inventoryIdStr));
                
                if (productOpt.isPresent() && invOpt.isPresent()) {
                    ProductIngredient ingredient = ProductIngredient.builder()
                            .product(productOpt.get())
                            .inventoryItem(invOpt.get())
                            .quantityRequired(Double.parseDouble(quantityStr))
                            .build();
                    ingredientRepository.save(ingredient);
                    return CafeUtils.getResponseEntity("Ingredient Added to Product", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Product or Inventory Item not found", HttpStatus.NOT_FOUND);
                }
            }
            return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        } catch (NumberFormatException e) {
            log.error("Invalid number format in ingredient request", e);
            return CafeUtils.getResponseEntity("Invalid numbers provided", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error adding ingredient to product", e);
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<String> removeIngredientFromProduct(Integer id) {
        try {
            ingredientRepository.deleteById(id);
            return CafeUtils.getResponseEntity("Ingredient Removed Successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error removing ingredient", e);
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateInventoryMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name") && requestMap.containsKey("quantity") && requestMap.containsKey("unit")) {
            if (validateId) {
                return requestMap.containsKey("id");
            }
            return true;
        }
        return false;
    }

    private InventoryItem getInventoryFromMap(Map<String, String> requestMap, boolean isAdd) {
        InventoryItem item = new InventoryItem();
        if (isAdd) {
            item.setId(Integer.parseInt(requestMap.get("id")));
        }
        item.setName(requestMap.get("name"));
        item.setQuantity(Double.parseDouble(requestMap.get("quantity")));
        item.setUnit(requestMap.get("unit"));
        if (requestMap.containsKey("threshold")) {
            item.setLowStockThreshold(Double.parseDouble(requestMap.get("threshold")));
        } else {
            item.setLowStockThreshold(0.0);
        }
        return item;
    }
}
