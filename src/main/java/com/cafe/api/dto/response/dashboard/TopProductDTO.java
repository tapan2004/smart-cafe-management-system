package com.cafe.api.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopProductDTO {
    private Integer productId;
    private String name;
    private Long totalSold;
}