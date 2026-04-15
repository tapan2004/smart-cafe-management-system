package com.cafe.api.controller;

import com.cafe.api.dto.request.CategoryRequestDTO;
import com.cafe.api.dto.response.CategoryResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping("/category")
public interface CategoryController {
    @PostMapping("/add")
    ResponseEntity<String> addCategory(@RequestBody CategoryRequestDTO request);

    @GetMapping("/get")
    ResponseEntity<List<CategoryResponseDTO>> getAllCategories();

    @PutMapping("/update")
    ResponseEntity<String> updateCategory(@RequestBody CategoryRequestDTO request);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<String> deleteCategory(@PathVariable Integer id);
}