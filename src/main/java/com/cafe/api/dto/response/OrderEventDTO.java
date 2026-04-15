package com.cafe.api.dto.response;

import com.cafe.api.entity.bill.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventDTO {
    private String uuid;
    private String customerName;
    private String tableNumber; // For future use
    private OrderStatus status;
    private List<OrderItemDTO> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private String productName;
        private Integer quantity;
        private String categoryName;
    }
}
