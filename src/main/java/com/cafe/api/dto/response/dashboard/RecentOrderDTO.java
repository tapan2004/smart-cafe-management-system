package com.cafe.api.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecentOrderDTO {

    private String uuid;
    private String name;
    private Double total;
    private String createdBy;
}
