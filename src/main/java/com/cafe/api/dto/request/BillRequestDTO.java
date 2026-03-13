package com.cafe.api.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BillRequestDTO {

    private String name;
    private String email;
    private String contactNumber;
    private String paymentMethod;
 //   private String productDetails;
    private Boolean isGenerate;
    private String uuid;
   // private List<BillItemRequestDTO> products;
    private List<BillItemRequestDTO> items;
}