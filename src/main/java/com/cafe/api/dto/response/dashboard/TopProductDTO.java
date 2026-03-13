package com.cafe.api.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProductDTO {
    private String name;
    private Long totalSold;
}