package com.cafe.api.service.impl;

import com.cafe.api.constant.CafeConstants;
import com.cafe.api.dto.request.BillItemRequestDTO;
import com.cafe.api.dto.request.BillRequestDTO;
import com.cafe.api.entity.bill.Bill;
import com.cafe.api.entity.bill.BillItem;
import com.cafe.api.entity.product.Product;
import com.cafe.api.repository.BillRepository;
import com.cafe.api.repository.ProductRepository;
import com.cafe.api.security.JwtFilter;
import com.cafe.api.service.BillService;
import com.cafe.api.util.CafeUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Optional;

import static com.google.zxing.client.j2se.MatrixToImageWriter.toBufferedImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {
    private static final Logger log = LoggerFactory.getLogger(BillServiceImpl.class);

    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final com.cafe.api.repository.InventoryRepository inventoryRepository;
    private final com.cafe.api.repository.IngredientRepository ingredientRepository;
    private final JwtFilter jwtFilter;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;
    private final com.cafe.api.service.EmailService emailService;
    private final com.cafe.api.repository.AuditLogRepository auditLogRepository;

    // Cache to prevent email flooding (Stock ID -> Last Email Time)
    private final java.util.Map<Integer, Long> emailAlertTracker = new java.util.concurrent.ConcurrentHashMap<>();
    private static final long EMAIL_THROTTLE_MS = 3600000; // 1 Hour Throttling

    // GENERATE REPORT
    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<String> generateReport(BillRequestDTO request) {

        log.info("Inside generateReport");
        try {
            if (!validateRequest(request)) {
                return CafeUtils.getResponseEntity(
                        "Required Data Missing",
                        HttpStatus.BAD_REQUEST
                );
            }

            String fileName;
            if (request.getIsGenerate() != null && !request.getIsGenerate()) {
                fileName = request.getUuid();
            } else {
                fileName = CafeUtils.getUID();
                request.setUuid(fileName);
                insertBill(request);
            }

            byte[] pdfBytes = generateInvoicePdf(request);
            File directory = new File(CafeConstants.STORE_LOCATION);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            try (FileOutputStream file =
                         new FileOutputStream(CafeConstants.STORE_LOCATION + File.separator + fileName + ".pdf")) {
                file.write(pdfBytes);
            }

            // Log to Audit Trail
            auditLogRepository.save(new com.cafe.api.entity.AuditLog(
                "BILL_GENERATED", 
                jwtFilter.getCurrentUser(), 
                "Generated Bill ID: " + fileName
            ));

            // BROADCAST TO KITCHEN
            broadcastOrderToKitchen(request);

            return CafeUtils.getResponseEntity(
                    "Report Generated Successfully ID : " + fileName,
                    HttpStatus.OK
            );

        } catch (Throwable e) {
            log.error("CRITICAL ERROR in generateReport: {}", e.getMessage(), e);
            return CafeUtils.getResponseEntity(
                    CafeConstants.SOMETHING_WENT_WRONG,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    // GENERATE PDF
    private byte[] generateInvoicePdf(BillRequestDTO request) throws Exception {
        Optional<Bill> billOptional = billRepository.findBillWithItems(request.getUuid());
        Bill bill = billOptional.orElseThrow(() -> new RuntimeException("Bill not found"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        addHeader(document);
        addCustomerSection(document, request);
        addDivider(document);
        addProductTable(document, request);
        addBillingSummary(document, request);
        addUPIQR(document, request);
        addFooter(document);

        document.close();
        return outputStream.toByteArray();
    }

    // HEADER
    private void addHeader(Document document) throws Exception {
        BaseColor coffeeColor = new BaseColor(163, 128, 104);
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, coffeeColor);
        Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.GRAY);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        PdfPCell titleCell = new PdfPCell(new Phrase("CAFEFLOW OPERATIONAL RECEIPT", titleFont));
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(titleCell);

        PdfPCell subTitleCell = new PdfPCell(new Phrase("PREMIUM BREWS & DIGITAL TRANSACTIONS", subTitleFont));
        subTitleCell.setBorder(Rectangle.NO_BORDER);
        subTitleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        subTitleCell.setPaddingBottom(20);
        table.addCell(subTitleCell);

        document.add(table);
        
        // Add a line separator
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(coffeeColor);
        ls.setLineWidth(2f);
        document.add(new Chunk(ls));
        document.add(new Paragraph(" "));
    }

    // CUSTOMER DETAILS
    private void addCustomerSection(Document document, BillRequestDTO request) throws Exception {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        Font bold = new Font(
                Font.FontFamily.HELVETICA, 12, Font.BOLD);

        PdfPCell left = new PdfPCell();
        left.setBorder(Rectangle.NO_BORDER);

        left.addElement(new Paragraph("Customer Details", bold));
        left.addElement(new Paragraph("Name: " + request.getName()));
        left.addElement(new Paragraph("Email: " + request.getEmail()));
        left.addElement(new Paragraph("Contact: " + request.getContactNumber()));
        left.addElement(new Paragraph("Payment: " + request.getPaymentMethod()));

        table.addCell(left);
        PdfPCell right = new PdfPCell();
        right.setBorder(Rectangle.NO_BORDER);

        right.addElement(new Paragraph("Invoice Details", bold));
        right.addElement(new Paragraph("Invoice ID: " + request.getUuid()));
        right.addElement(new Paragraph("Date: " + java.time.LocalDate.now()));
        right.addElement(new Paragraph("Cashier: " + jwtFilter.getCurrentUser()));

        table.addCell(right);
        document.add(table);
        document.add(new Paragraph(" "));
    }

    // DIVIDER
    private void addDivider(Document document) throws Exception {
        LineSeparator line = new LineSeparator();
        document.add(new Chunk(line));
        document.add(new Paragraph(" "));
    }

    // PRODUCT TABLE
    private void addProductTable(Document document, BillRequestDTO request) throws Exception {

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{4, 3, 2, 2, 2});
        Font headerFont = new Font(
                Font.FontFamily.HELVETICA, 12, Font.BOLD);

        table.addCell(createHeader("Item", headerFont));
        table.addCell(createHeader("Category", headerFont));
        table.addCell(createHeader("Qty", headerFont));
        table.addCell(createHeader("Price", headerFont));
        table.addCell(createHeader("Total", headerFont));

        Optional<Bill> billOptional = billRepository.findBillWithItems(request.getUuid());

        if (billOptional.isPresent()) {
            Bill bill = billOptional.get();

            for (BillItem item : bill.getItems()) {
                Product product = item.getProduct();

                table.addCell(product.getName());
                table.addCell(product.getCategory().getName());
                table.addCell(String.valueOf(item.getQuantity()));

                PdfPCell price = new PdfPCell(new Phrase("₹ " + item.getPrice()));
                price.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(price);

                PdfPCell total = new PdfPCell(new Phrase("₹ " + item.getTotal()));
                total.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(total);
            }
        }
        document.add(table);
    }

    private PdfPCell createHeader(String text, Font font) {
        BaseColor coffeeColor = new BaseColor(163, 128, 104);
        PdfPCell cell = new PdfPCell(new Phrase(text.toUpperCase(), new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE)));

        cell.setBackgroundColor(coffeeColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        cell.setBorderColor(BaseColor.WHITE);
        return cell;
    }

    // BILL SUMMARY
    private void addBillingSummary(Document document, BillRequestDTO request) throws Exception {
        BaseColor coffeeColor = new BaseColor(163, 128, 104);
        Optional<Bill> billOptional = billRepository.findBillWithItems(request.getUuid());

        double subtotal = billOptional.map(Bill::getTotal).orElse(0.0);
        double tax = subtotal * 0.05; // 5% Special Service Tax
        double total = subtotal + tax;

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(40);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setSpacingBefore(20);

        Font normal = new Font(Font.FontFamily.HELVETICA, 10);
        Font boldWhite = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);

        addSummaryRow(table, "Gross Value", String.format("₹ %.2f", subtotal), normal);
        addSummaryRow(table, "Service Tax (5%)", String.format("₹ %.2f", tax), normal);

        PdfPCell totalLabel = new PdfPCell(new Phrase("GRAND TOTAL", boldWhite));
        totalLabel.setBackgroundColor(coffeeColor);
        totalLabel.setPadding(10);
        totalLabel.setBorder(Rectangle.NO_BORDER);

        PdfPCell totalVal = new PdfPCell(new Phrase(String.format("₹ %.2f", total), boldWhite));
        totalVal.setBackgroundColor(coffeeColor);
        totalVal.setPadding(10);
        totalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalVal.setBorder(Rectangle.NO_BORDER);

        table.addCell(totalLabel);
        table.addCell(totalVal);
        
        document.add(table);
    }

    private void addSummaryRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell lCell = new PdfPCell(new Phrase(label, font));
        lCell.setBorder(Rectangle.NO_BORDER);
        lCell.setPaddingBottom(5);
        
        PdfPCell vCell = new PdfPCell(new Phrase(value, font));
        vCell.setBorder(Rectangle.NO_BORDER);
        vCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        vCell.setPaddingBottom(5);
        
        table.addCell(lCell);
        table.addCell(vCell);
    }

    // UPI QR
    private void addUPIQR(Document document, BillRequestDTO request) throws Exception {
        Optional<Bill> billOptional = billRepository.findByUuid(request.getUuid());
        double total = billOptional.map(b -> b.getTotal() * 1.05).orElse(0.0); // Total with tax
        
        // Dynamic UPI Link: upi://pay?pa=VPA&pn=NAME&am=AMOUNT&cu=CURRENCY
        String upiPayload = String.format("upi://pay?pa=cafeflow@upi&pn=CafeFlow&am=%.2f&cu=INR", total);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(upiPayload, BarcodeFormat.QR_CODE, 180, 180);
        BufferedImage image = toBufferedImage(matrix);

        Image qr = Image.getInstance(image, null);
        qr.setAlignment(Element.ALIGN_CENTER);

        document.add(new Paragraph(" "));
        document.add(qr);

        Paragraph scan = new Paragraph("Scan to Pay via UPI");
        scan.setAlignment(Element.ALIGN_CENTER);
        document.add(scan);
    }

    // FOOTER
    private void addFooter(Document document) throws Exception {

        document.add(new Paragraph(" "));
        LineSeparator line = new LineSeparator();
        document.add(new Chunk(line));
        Font footer = new Font(Font.FontFamily.HELVETICA, 10);

        Paragraph thanks = new Paragraph("Thank you for visiting ☕", footer);
        thanks.setAlignment(Element.ALIGN_CENTER);

        Paragraph powered = new Paragraph(
                "Powered by Cafe Management System",
                new Font(Font.FontFamily.HELVETICA, 8)
        );

        powered.setAlignment(Element.ALIGN_CENTER);
        document.add(thanks);
        document.add(powered);
    }

    // INSERT BILL
    private void insertBill(BillRequestDTO request) {

        try {
            Bill bill = new Bill();

            bill.setUuid(request.getUuid());
            bill.setName(request.getName().trim());
            bill.setEmail(request.getEmail().trim());
            bill.setContactNumber(request.getContactNumber().trim());
            bill.setPaymentMethod(request.getPaymentMethod());
            bill.setCreatedBy(jwtFilter.getCurrentUser());

            double total = 0;
            for (BillItemRequestDTO itemDTO : request.getItems()) {

                Product product = productRepository
                        .findById(itemDTO.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                BillItem item = new BillItem();

                item.setProduct(product);
                item.setQuantity(itemDTO.getQuantity());
                item.setPrice(product.getPrice());

                double itemTotal = product.getPrice() * itemDTO.getQuantity();

                item.setTotal(itemTotal);
                item.setBill(bill);
                bill.getItems().add(item);
                total += itemTotal;
            }

            bill.setTotal(total);
            billRepository.saveAndFlush(bill);

            // PHASE 2: INVENTORY DEDUCTION
            deductInventory(request);
            
            // Log to Audit Trail
            auditLogRepository.save(new com.cafe.api.entity.AuditLog(
                "BILL_GENERATED", 
                jwtFilter.getCurrentUser(), 
                "Generated Bill ID: " + bill.getUuid() + " for total ₹" + bill.getTotal()
            ));

        } catch (Exception e) {
            log.error("Error in insertBill: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to insert bill", e);
        }
    }

    private void deductInventory(BillRequestDTO request) {
        for (BillItemRequestDTO itemDTO : request.getItems()) {
            List<com.cafe.api.entity.inventory.ProductIngredient> ingredients = 
                ingredientRepository.findByProductId(itemDTO.getProductId());
            
            for (com.cafe.api.entity.inventory.ProductIngredient ingredient : ingredients) {
                com.cafe.api.entity.inventory.InventoryItem inventoryItem = ingredient.getInventoryItem();
                if (inventoryItem == null) continue;

                double totalReduction = ingredient.getQuantityRequired() * itemDTO.getQuantity();
                double currentQty = (inventoryItem.getQuantity() != null) ? inventoryItem.getQuantity() : 0.0;
                
                inventoryItem.setQuantity(currentQty - totalReduction);
                inventoryRepository.save(inventoryItem);
                
                if (inventoryItem.getQuantity() <= (inventoryItem.getLowStockThreshold() != null ? inventoryItem.getLowStockThreshold() : 0.0)) {
                    log.warn("LOW STOCK ALERT: {} is at {}{}", 
                        inventoryItem.getName(), inventoryItem.getQuantity(), inventoryItem.getUnit());
                    
                    sendStockAlertEmail(inventoryItem);
                }
            }
        }
    }

    private void sendStockAlertEmail(com.cafe.api.entity.inventory.InventoryItem item) {
        long now = System.currentTimeMillis();
        Long lastEmail = emailAlertTracker.get(item.getId());
        
        if (lastEmail == null || (now - lastEmail) > EMAIL_THROTTLE_MS) {
            try {
                String subject = "CRITICAL STOCK ALERT: " + item.getName();
                String body = String.format(
                    "Tactical Inventory Update:\n\n" +
                    "Item: %s\n" +
                    "Current Level: %.2f %s\n" +
                    "Threshold: %.2f %s\n\n" +
                    "Action Required: RESTOCK IMMEDIATELY to prevent operational downtime.",
                    item.getName(), item.getQuantity(), item.getUnit(), 
                    item.getLowStockThreshold(), item.getUnit()
                );
                
                emailService.sendSimpleMessage("admin@cafe.com", subject, body);
                emailAlertTracker.put(item.getId(), now);
                log.info("Stock alert email dispatched for {}", item.getName());
            } catch (Exception e) {
                log.error("Failed to send stock alert email for {}", item.getName(), e);
            }
        }
    }

    // VALIDATE REQUEST
    private boolean validateRequest(BillRequestDTO request) {

        return request.getName() != null &&
                request.getContactNumber() != null &&
                request.getEmail() != null &&
                request.getPaymentMethod() != null &&
                request.getItems() != null &&
                !request.getItems().isEmpty();
    }

    // GET BILLS
    @Override
    public ResponseEntity<List<Bill>> getBills() {

        List<Bill> bills;
        if (jwtFilter.isAdmin()) {
            bills = billRepository.getAllBills();
        } else {
            bills = billRepository.getBillByUserName(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

    // GET PDF
    @Override
    public ResponseEntity<byte[]> getPdf(String uuid) {
        log.info("Inside getPdf : {}", uuid);

        try {
            Optional<Bill> billOptional = billRepository.findBillWithItems(uuid);
            if (billOptional.isPresent()) {
                BillRequestDTO request = getRequest(billOptional);

                byte[] pdfBytes = generateInvoicePdf(request);
                HttpHeaders headers = new HttpHeaders();

                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDisposition(
                        ContentDisposition.inline()
                                .filename("invoice_" + uuid + ".pdf")
                                .build()
                );
                return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {

            log.error("Error generating PDF", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static @NonNull BillRequestDTO getRequest(Optional<Bill> billOptional) {
        Bill bill = billOptional.orElseThrow(() ->
                new RuntimeException("Bill not found"));

        BillRequestDTO request = new BillRequestDTO();

        request.setName(bill.getName());
        request.setEmail(bill.getEmail());
        request.setContactNumber(bill.getContactNumber());
        request.setPaymentMethod(bill.getPaymentMethod());
        request.setUuid(bill.getUuid());
        return request;
    }

    // DELETE BILL
    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            Optional<Bill> optional = billRepository.findById(id);
            if (optional.isPresent()) {
                billRepository.deleteById(id);

                return CafeUtils.getResponseEntity(
                        "Bill Deleted Successfully",
                        HttpStatus.OK
                );
            }
        } catch (Exception e) {
            log.error("Error in deleteBill", e);
        }
        return CafeUtils.getResponseEntity(
                "Something Went Wrong",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private void broadcastOrderToKitchen(BillRequestDTO request) {
        try {
            com.cafe.api.dto.response.OrderEventDTO event = com.cafe.api.dto.response.OrderEventDTO.builder()
                    .uuid(request.getUuid())
                    .customerName(request.getName())
                    .status(com.cafe.api.entity.bill.OrderStatus.PLACED)
                    .items(request.getItems().stream().map(item -> {
                        Product product = productRepository.findById(item.getProductId()).orElse(null);
                        return com.cafe.api.dto.response.OrderEventDTO.OrderItemDTO.builder()
                                .productName(product != null ? product.getName() : "Unknown")
                                .quantity(item.getQuantity())
                                .categoryName(product != null && product.getCategory() != null ? product.getCategory().getName() : "General")
                                .build();
                    }).toList())
                    .build();

            log.info("Broadcasting order to kitchen: {}", request.getUuid());
            messagingTemplate.convertAndSend("/topic/kitchen", event);
        } catch (Exception e) {
            log.error("Failed to broadcast order to kitchen", e);
        }
    }

    @Override
    public ResponseEntity<List<com.cafe.api.dto.response.OrderEventDTO>> getActiveOrders() {
        try {
            List<com.cafe.api.entity.bill.Bill> activeBills = billRepository.findByStatusNot(com.cafe.api.entity.bill.OrderStatus.COMPLETED);
            List<com.cafe.api.dto.response.OrderEventDTO> orderEvents = activeBills.stream().map(bill -> 
                com.cafe.api.dto.response.OrderEventDTO.builder()
                    .uuid(bill.getUuid())
                    .customerName(bill.getName())
                    .status(bill.getStatus())
                    .items(bill.getItems().stream().map(item -> 
                        com.cafe.api.dto.response.OrderEventDTO.OrderItemDTO.builder()
                                .productName(item.getProduct().getName())
                                .quantity(item.getQuantity())
                                .categoryName(item.getProduct().getCategory().getName())
                                .build()
                    ).toList())
                    .build()
            ).toList();
            return new ResponseEntity<>(orderEvents, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching active orders", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}