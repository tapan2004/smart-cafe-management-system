package com.cafe.api.service;

import com.cafe.api.dto.request.CategoryRequestDTO;
import com.cafe.api.dto.response.CategoryResponseDTO;
import com.cafe.api.entity.category.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    ResponseEntity<String> addCategory(CategoryRequestDTO request);

    ResponseEntity<List<CategoryResponseDTO>> getAllCategories();

    ResponseEntity<String> updateCategory(CategoryRequestDTO request);
}
