package com.cafe.api.dto.response;

import lombok.Data;

@Data
public class BillItemResponseDTO {

    private String productName;
    private Integer quantity;
    private Double price;
    private Double total;
}