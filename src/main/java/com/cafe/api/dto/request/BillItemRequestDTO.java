package com.cafe.api.dto.request;

import lombok.Data;

@Data
public class BillItemRequestDTO {
    private Integer productId;
    private Integer quantity;
}
