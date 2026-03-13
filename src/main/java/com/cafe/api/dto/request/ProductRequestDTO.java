package com.cafe.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class ProductRequestDTO {
    private Integer id;
    @NotBlank
    private String name;
    @NonNull
    private Integer categoryId;
    private Double price;
    private Boolean status;
    private String description;
}
