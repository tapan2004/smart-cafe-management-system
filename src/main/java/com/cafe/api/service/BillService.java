package com.cafe.api.service;

import com.cafe.api.dto.request.BillRequestDTO;
import com.cafe.api.entity.bill.Bill;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BillService {

    ResponseEntity<String> generateReport(BillRequestDTO request);

    ResponseEntity<List<Bill>> getBills();

    ResponseEntity<byte[]> getPdf(String request);

    ResponseEntity<String> deleteBill(Integer id);
}