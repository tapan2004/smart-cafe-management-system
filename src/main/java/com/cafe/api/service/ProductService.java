package com.cafe.api.service;

import com.cafe.api.dto.request.ProductRequestDTO;
import com.cafe.api.dto.response.ProductResponseDTO;
import com.cafe.api.entity.product.Product;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {
    ResponseEntity<String> addProduct(ProductRequestDTO request);

    ResponseEntity<List<ProductResponseDTO>> getAllProducts();

    ResponseEntity<String> updateProduct(ProductRequestDTO request);

    ResponseEntity<String> deleteProduct(Integer id);

    ResponseEntity<String> updateStatus(Integer id,Boolean status);

    ResponseEntity<List<ProductResponseDTO>> getByCategory(Integer id);

    ResponseEntity<ProductResponseDTO> getProductById(Integer id);
}