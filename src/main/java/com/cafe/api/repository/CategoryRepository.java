package com.cafe.api.repository;

import com.cafe.api.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("select c from Category c where c.isDeleted = false order by c.name")
    List<Category> getAllCategory();

    @Override
    Optional<Category> findById(Integer integer);
}