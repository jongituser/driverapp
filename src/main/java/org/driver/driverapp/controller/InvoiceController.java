package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.invoice.request.CreateInvoiceRequestDTO;
import org.driver.driverapp.dto.invoice.request.UpdateInvoiceRequestDTO;
import org.driver.driverapp.dto.invoice.response.InvoiceResponseDTO;
import org.driver.driverapp.enums.InvoiceStatus;
import org.driver.driverapp.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<InvoiceResponseDTO> createInvoice(@Valid @RequestBody CreateInvoiceRequestDTO requestDTO) {
        log.info("Creating invoice for partner: {}", requestDTO.getPartnerId());
        
        InvoiceResponseDTO response = invoiceService.createInvoice(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<InvoiceResponseDTO> updateInvoice(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInvoiceRequestDTO requestDTO) {
        log.info("Updating invoice: {}", id);
        
        InvoiceResponseDTO response = invoiceService.updateInvoice(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/mark-paid")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<InvoiceResponseDTO> markInvoiceAsPaid(
            @PathVariable Long id,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String paymentReference) {
        log.info("Marking invoice as paid: {}, amount: {}", id, amount);
        
        InvoiceResponseDTO response = invoiceService.markInvoiceAsPaid(id, amount, paymentReference);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/send")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<InvoiceResponseDTO> sendInvoice(@PathVariable Long id) {
        log.info("Sending invoice: {}", id);
        
        InvoiceResponseDTO response = invoiceService.sendInvoice(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceById(@PathVariable Long id) {
        InvoiceResponseDTO response = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{invoiceNumber}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<InvoiceResponseDTO> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        InvoiceResponseDTO response = invoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/partner/{partnerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<InvoiceResponseDTO>> getInvoicesByPartner(
            @PathVariable Long partnerId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<InvoiceResponseDTO> response = invoiceService.getInvoicesByPartner(partnerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<InvoiceResponseDTO>> getInvoicesByStatus(
            @PathVariable InvoiceStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<InvoiceResponseDTO> response = invoiceService.getInvoicesByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/due-date/{dueDate}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<InvoiceResponseDTO>> getInvoicesByDueDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        List<InvoiceResponseDTO> response = invoiceService.getInvoicesByDueDate(dueDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<InvoiceResponseDTO>> getOverdueInvoices() {
        List<InvoiceResponseDTO> response = invoiceService.getOverdueInvoices();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/due-soon")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<InvoiceResponseDTO>> getInvoicesDueSoon() {
        List<InvoiceResponseDTO> response = invoiceService.getInvoicesDueSoon();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unpaid")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<InvoiceResponseDTO>> getUnpaidInvoices() {
        List<InvoiceResponseDTO> response = invoiceService.getUnpaidInvoices();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/partner/{partnerId}/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getTotalInvoicesByPartner(@PathVariable Long partnerId) {
        BigDecimal total = invoiceService.getTotalInvoicesByPartner(partnerId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/partner/{partnerId}/paid")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getPaidAmountByPartner(@PathVariable Long partnerId) {
        BigDecimal paid = invoiceService.getPaidAmountByPartner(partnerId);
        return ResponseEntity.ok(paid);
    }

    @GetMapping("/status/{status}/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getTotalInvoicesByStatus(@PathVariable InvoiceStatus status) {
        BigDecimal total = invoiceService.getTotalInvoicesByStatus(status);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/generate-from-deliveries")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<InvoiceResponseDTO> generateInvoiceFromDeliveries(
            @RequestParam Long partnerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) String description) {
        log.info("Generating invoice from deliveries for partner: {}", partnerId);
        
        InvoiceResponseDTO response = invoiceService.generateInvoiceFromDeliveries(partnerId, dueDate, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/mark-overdue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> markOverdueInvoices() {
        log.info("Marking overdue invoices");
        
        invoiceService.markOverdueInvoices();
        return ResponseEntity.ok().build();
    }
}
