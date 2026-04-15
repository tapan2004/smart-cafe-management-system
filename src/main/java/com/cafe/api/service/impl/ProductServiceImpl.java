package com.cafe.api.service.impl;

import com.cafe.api.dto.request.ProductRequestDTO;
import com.cafe.api.dto.response.ProductResponseDTO;
import com.cafe.api.entity.category.Category;
import com.cafe.api.entity.product.Product;
import com.cafe.api.repository.CategoryRepository;
import com.cafe.api.repository.ProductRepository;
import com.cafe.api.security.JwtFilter;
import com.cafe.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<String> addProduct(ProductRequestDTO request) {

//        Category category=new Category();
//        category.setId(request.getCategoryId());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(category);
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStatus(true);

        productRepository.save(product);

        return ResponseEntity.ok("Product Added Successfully");
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return ResponseEntity.ok(productRepository.getAllProducts());
    }

    @Override
    public ResponseEntity<String> updateProduct(ProductRequestDTO request) {
        Optional<Product> optional = productRepository.findById(request.getId());
        if (optional.isEmpty()) {
            return new ResponseEntity<>("Product Not Found", HttpStatus.NOT_FOUND);
        }
        Product product = optional.get();

//        Category category = new Category();
//        category.setId(request.getCategoryId());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setStatus(request.getStatus());

        productRepository.save(product);

        return ResponseEntity.ok("Product Updated Successfully");
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProduct(Integer id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok("Product Deleted Successfully");
    }

    @Override
    public ResponseEntity<String> updateStatus(Integer id, Boolean status) {
        Optional<Product> optional = productRepository.findById(id);

        if (optional.isEmpty()) {
            return new ResponseEntity<>("Product Not Found", HttpStatus.NOT_FOUND);
        }
        Product product = optional.get();
        product.setStatus(status);
        productRepository.save(product);
        return ResponseEntity.ok("Status Updated");
    }

    @Override
    public ResponseEntity<List<ProductResponseDTO>> getByCategory(Integer id) {
        return ResponseEntity.ok(productRepository.getProductsByCategory(id));
    }

    @Override
    public ResponseEntity<ProductResponseDTO> getProductById(Integer id) {
        Optional<Product> optional = productRepository.findById(id);

        if (optional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Product p = optional.get();

        ProductResponseDTO dto = new ProductResponseDTO(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStatus(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getCreatedAt()
        );
        return ResponseEntity.ok(dto);
    }
}