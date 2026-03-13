package com.cafe.api.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequestDTO {

    private Integer id;

    @NotBlank(message = "Category name is required")
    private String name;
}