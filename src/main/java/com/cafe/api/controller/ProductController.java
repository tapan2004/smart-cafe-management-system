package com.cafe.api.controller;

import com.cafe.api.dto.request.ProductRequestDTO;
import com.cafe.api.dto.response.ProductResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/product")
public interface ProductController {

    @PostMapping("/add")
    ResponseEntity<String> addProduct(@RequestBody ProductRequestDTO request);

    @GetMapping("/get")
    ResponseEntity<List<ProductResponseDTO>> getAllProducts();

    @PutMapping("/update")
    ResponseEntity<String> updateProduct(@RequestBody ProductRequestDTO request);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable Integer id);

    @PutMapping("/updateStatus")
    ResponseEntity<String> updateStatus(@RequestParam Integer id,
                                        @RequestParam Boolean status);

    @GetMapping("/category/{id}")
    ResponseEntity<List<ProductResponseDTO>> getByCategory(@PathVariable Integer id);

    @GetMapping("/{id}")
    ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Integer id);
}