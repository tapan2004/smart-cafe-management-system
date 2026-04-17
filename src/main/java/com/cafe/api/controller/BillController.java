package com.cafe.api.controller;

import com.cafe.api.dto.request.BillRequestDTO;
import com.cafe.api.entity.bill.Bill;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/bill")
public interface BillController {

    @GetMapping("/getBills")
    ResponseEntity<List<Bill>> getBills();

    @PostMapping("/generateReport")
    ResponseEntity<String> generateReport(@RequestBody BillRequestDTO request);

    @GetMapping("/getPdf/{uuid}")
    ResponseEntity<byte[]> getPdf(@PathVariable String uuid);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<String> deleteBill(@PathVariable Integer id);

    @GetMapping("/getActiveOrders")
    ResponseEntity<List<com.cafe.api.dto.response.OrderEventDTO>> getActiveOrders();

    @PostMapping("/public/placeOrder")
    ResponseEntity<String> placePublicOrder(@RequestBody BillRequestDTO request);
}