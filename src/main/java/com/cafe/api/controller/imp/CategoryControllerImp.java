package com.cafe.api.controller.imp;

import com.cafe.api.controller.CategoryController;
import com.cafe.api.dto.request.CategoryRequestDTO;
import com.cafe.api.dto.response.CategoryResponseDTO;
import com.cafe.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryControllerImp implements CategoryController {
    private final CategoryService categoryService;

    @Override
    public ResponseEntity<String> addCategory(CategoryRequestDTO request) {
        return categoryService.addCategory(request);
    }

    @Override
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @Override
    public ResponseEntity<String> updateCategory(CategoryRequestDTO request) {
        return categoryService.updateCategory(request);
    }

    @Override
    public ResponseEntity<String> deleteCategory(Integer id) {
        return categoryService.deleteCategory(id);
    }
}