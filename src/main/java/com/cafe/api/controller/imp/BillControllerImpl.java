package com.cafe.api.controller.imp;

import com.cafe.api.controller.BillController;
import com.cafe.api.dto.request.BillRequestDTO;
import com.cafe.api.entity.bill.Bill;
import com.cafe.api.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BillControllerImpl implements BillController {

    private final BillService billService;

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        return billService.getBills();
    }

    @Override
    public ResponseEntity<String> generateReport(BillRequestDTO request) {
        return billService.generateReport(request);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(String uuid) {
        return billService.getPdf(uuid);
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        return billService.deleteBill(id);
    }

    @Override
    public ResponseEntity<List<com.cafe.api.dto.response.OrderEventDTO>> getActiveOrders() {
        return billService.getActiveOrders();
    }
}