package com.cafe.api.service.impl;

import com.cafe.api.dto.request.CategoryRequestDTO;
import com.cafe.api.dto.response.CategoryResponseDTO;
import com.cafe.api.entity.category.Category;
import com.cafe.api.repository.CategoryRepository;
import com.cafe.api.security.JwtFilter;
import com.cafe.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    //private final JwtFilter jwtFilter;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addCategory(CategoryRequestDTO request) {

        log.info("Inside addCategory");

        Category category = new Category();
        category.setName(request.getName());

        categoryRepository.save(category);

        return ResponseEntity.ok("Category Added Successfully");
    }

    @Override
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        log.info("Inside getAllCategories");

        List<CategoryResponseDTO> categories =
                categoryRepository.getAllCategory()
                        .stream()
                        .map(c -> new CategoryResponseDTO(
                                c.getId(),
                                c.getName()))
                        .collect(Collectors.toList());

        return ResponseEntity.ok(categories);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateCategory(CategoryRequestDTO request) {
        log.info("Inside updateCategory");

        if (request.getId() == null) {
            return new ResponseEntity<>("Category ID Required", HttpStatus.BAD_REQUEST);
        }

        Optional<Category> optional =
                categoryRepository.findById(request.getId());

        if (optional.isEmpty()) {
            return new ResponseEntity<>("Category Not Found", HttpStatus.NOT_FOUND);
        }

        Category category = optional.get();
        category.setName(request.getName());

        categoryRepository.save(category);

        return ResponseEntity.ok("Category Updated Successfully");
    }
}