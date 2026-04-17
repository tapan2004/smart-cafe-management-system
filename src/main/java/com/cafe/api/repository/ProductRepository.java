package com.cafe.api.repository;

import com.cafe.api.dto.response.ProductResponseDTO;
import com.cafe.api.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("""
            SELECT new com.cafe.api.dto.response.ProductResponseDTO(
             p.id,p.name,p.description,p.price,p.status,
             p.category.id,p.category.name,
             p.createdAt
             )
            FROM Product p
            WHERE p.isDeleted = false
            """)
    List<ProductResponseDTO> getAllProducts();

    @Query("""

        SELECT new com.cafe.api.dto.response.ProductResponseDTO(
        p.id,p.name,p.description,p.price,p.status,
        p.category.id,p.category.name,
        p.createdAt
        )
        FROM Product p
        WHERE p.category.id=:id AND p.status=true AND p.isDeleted = false
        """)
    List<ProductResponseDTO> getProductsByCategory(Integer id);
}