package com.cafe.api.dto.request;

import com.cafe.api.entity.bill.OrderStatus;
import lombok.Data;

@Data
public class StatusUpdateDTO {
    private String uuid;
    private OrderStatus status;
}
