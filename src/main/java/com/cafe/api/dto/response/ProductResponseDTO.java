package com.cafe.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Boolean status;
    private Integer categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}