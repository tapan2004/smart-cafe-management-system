package com.cafe.api.repository;

import com.cafe.api.entity.inventory.ProductIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<ProductIngredient, Integer> {
    List<ProductIngredient> findByProductId(Integer productId);
}
