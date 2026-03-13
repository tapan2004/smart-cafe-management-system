package com.cafe.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillResponseDTO {

    private String uuid;
    private String name;
    private String email;
    private String contactNumber;
    private String paymentMethod;
    private Double totalAmount;
    private List<BillItemResponseDTO> items;
}