package com.cafe.api.controller;

import com.cafe.api.dto.request.StatusUpdateDTO;
import com.cafe.api.entity.bill.Bill;
import com.cafe.api.entity.bill.OrderStatus;
import com.cafe.api.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class OrderWebSocketController {

    private final BillRepository billRepository;

    @MessageMapping("/update-status")
    @SendTo("/topic/order-updates")
    public StatusUpdateDTO updateOrderStatus(StatusUpdateDTO update) {
        log.info("Updating order status: {} to {}", update.getUuid(), update.getStatus());
        
        Optional<Bill> billOptional = billRepository.findByUuid(update.getUuid());
        if (billOptional.isPresent()) {
            Bill bill = billOptional.get();
            bill.setStatus(update.getStatus());
            billRepository.save(bill);
        }
        
        return update;
    }
}
