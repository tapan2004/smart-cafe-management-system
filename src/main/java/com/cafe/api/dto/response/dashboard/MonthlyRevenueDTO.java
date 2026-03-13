package com.cafe.api.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyRevenueDTO {
    private Integer month;
    private Double revenue;
}
