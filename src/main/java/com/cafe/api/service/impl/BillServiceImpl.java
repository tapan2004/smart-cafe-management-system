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

@Slf4j
@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final ProductRepository productRepository;
    private final JwtFilter jwtFilter;

    // GENERATE REPORT
    @Override
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','STAFF')")
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
            try (FileOutputStream file =
                         new FileOutputStream(CafeConstants.STORE_LOCATION + "\\" + fileName + ".pdf")) {
                file.write(pdfBytes);
            }
            return CafeUtils.getResponseEntity(
                    "Report Generated Successfully ID : " + fileName,
                    HttpStatus.OK
            );

        } catch (Exception e) {
            log.error("Error in generateReport", e);
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
        Image logo = Image.getInstance("src/main/resources/static/logo.png");
        logo.scaleToFit(80, 80);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{1, 3});

        PdfPCell logoCell = new PdfPCell(logo);
        logoCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(logoCell);

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);

        PdfPCell textCell = new PdfPCell();
        textCell.setBorder(Rectangle.NO_BORDER);

        textCell.addElement(new Paragraph("Cafe Management System", titleFont));
        textCell.addElement(new Paragraph("Kolkata, India"));
        textCell.addElement(new Paragraph("Phone: +91 XXXXX XXXXX"));
        table.addCell(textCell);

        document.add(table);
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
        PdfPCell cell = new PdfPCell(new Phrase(text, font));

        cell.setBackgroundColor(new BaseColor(230, 230, 230));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6);
        return cell;
    }

    // BILL SUMMARY
    private void addBillingSummary(Document document, BillRequestDTO request) throws Exception {

        Optional<Bill> billOptional =
                billRepository.findBillWithItems(request.getUuid());

        double subtotal = billOptional.map(Bill::getTotal).orElse(0.0);
        double gst = subtotal * 0.18;
        double total = subtotal + gst;

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(35);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setSpacingBefore(10);

        table.addCell("Subtotal");
        table.addCell(String.format("₹ %.2f", subtotal));

        table.addCell("GST (18%)");
        table.addCell(String.format("₹ %.2f", gst));
        Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

        PdfPCell totalText = new PdfPCell(new Phrase("Total", bold));
        PdfPCell totalValue = new PdfPCell(new Phrase(String.format("₹ %.2f", total), bold));

        totalText.setBorder(Rectangle.TOP);
        totalValue.setBorder(Rectangle.TOP);
        totalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(totalText);
        table.addCell(totalValue);
        document.add(table);
    }

    // UPI QR
    private void addUPIQR(Document document, BillRequestDTO request) throws Exception {

        Optional<Bill> billOptional = billRepository.findBillWithItems(request.getUuid());
        double amount = billOptional.map(Bill::getTotal).orElse(0.0);

        String upi =
                "upi://pay?pa=cafe@upi&pn=CafeManagement&am=" + amount + "&cu=INR";

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(upi, BarcodeFormat.QR_CODE, 180, 180);
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
            bill.setName(request.getName());
            bill.setEmail(request.getEmail());
            bill.setContactNumber(request.getContactNumber());
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
            billRepository.save(bill);

        } catch (Exception e) {
            log.error("Error in insertBill", e);
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
}