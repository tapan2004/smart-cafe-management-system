package com.cafe.api.controller.imp;

import com.cafe.api.controller.ProductController;
import com.cafe.api.dto.request.ProductRequestDTO;
import com.cafe.api.dto.response.ProductResponseDTO;
import com.cafe.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductControllerImp implements ProductController {
    private final ProductService productService;

    @Override
    public ResponseEntity<String> addProduct(ProductRequestDTO request) {
        return productService.addProduct(request);
    }

    @Override
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return productService.getAllProducts();
    }

    @Override
    public ResponseEntity<String> updateProduct(ProductRequestDTO request) {
        return productService.updateProduct(request);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        return productService.deleteProduct(id);
    }

    @Override
    public ResponseEntity<String> updateStatus(Integer id, Boolean status) {
        return productService.updateStatus(id, status);
    }

    @Override
    public ResponseEntity<List<ProductResponseDTO>> getByCategory(Integer id) {
        return productService.getByCategory(id);
    }

    @Override
    public ResponseEntity<ProductResponseDTO> getProductById(Integer id) {
        return productService.getProductById(id);
    }
}