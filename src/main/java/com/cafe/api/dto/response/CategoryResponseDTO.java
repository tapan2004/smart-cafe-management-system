package com.cafe.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryResponseDTO {
    private Integer id;
    private String name;
}