package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.payment.request.ConfirmPaymentRequestDTO;
import org.driver.driverapp.dto.payment.request.InitiatePaymentRequestDTO;
import org.driver.driverapp.dto.payment.response.PaymentResponseDTO;
import org.driver.driverapp.enums.PaymentProvider;
import org.driver.driverapp.enums.PaymentStatus;
import org.driver.driverapp.service.PaymentService;
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
import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@Valid @RequestBody InitiatePaymentRequestDTO requestDTO) {
        log.info("Initiating payment for delivery: {}, provider: {}", requestDTO.getDeliveryId(), requestDTO.getProvider());
        
        PaymentResponseDTO response = paymentService.initiatePayment(
                requestDTO.getDeliveryId(), // Using deliveryId as userId for now
                requestDTO.getDeliveryId(),
                requestDTO.getProvider(),
                requestDTO.getAmount(),
                requestDTO.getPhoneNumber(),
                requestDTO.getDescription()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/confirm")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDTO> confirmPayment(@Valid @RequestBody ConfirmPaymentRequestDTO requestDTO) {
        log.info("Confirming payment with transaction ref: {}", requestDTO.getTransactionRef());
        
        PaymentResponseDTO response = paymentService.confirmPayment(requestDTO.getTransactionRef());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        PaymentResponseDTO response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transaction/{transactionRef}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<PaymentResponseDTO> getPaymentByTransactionRef(@PathVariable String transactionRef) {
        PaymentResponseDTO response = paymentService.getPaymentByTransactionRef(transactionRef);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #userId == authentication.principal.id")
    public ResponseEntity<Page<PaymentResponseDTO>> getPaymentsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PaymentResponseDTO> response = paymentService.getPaymentsByUser(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/delivery/{deliveryId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByDelivery(@PathVariable Long deliveryId) {
        List<PaymentResponseDTO> response = paymentService.getPaymentsByDelivery(deliveryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<PaymentResponseDTO>> getPaymentsByStatus(
            @PathVariable PaymentStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PaymentResponseDTO> response = paymentService.getPaymentsByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/provider/{provider}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<PaymentResponseDTO>> getPaymentsByProvider(
            @PathVariable PaymentProvider provider,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PaymentResponseDTO> response = paymentService.getPaymentsByProvider(provider, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        List<PaymentResponseDTO> response = paymentService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #userId == authentication.principal.id")
    public ResponseEntity<BigDecimal> getTotalPaymentsByUser(@PathVariable Long userId) {
        BigDecimal total = paymentService.getTotalPaymentsByUser(userId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/status/{status}/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getTotalPaymentsByStatus(@PathVariable PaymentStatus status) {
        BigDecimal total = paymentService.getTotalPaymentsByStatus(status);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/provider/{provider}/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getTotalPaymentsByProvider(@PathVariable PaymentProvider provider) {
        BigDecimal total = paymentService.getTotalPaymentsByProvider(provider);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/failed")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<PaymentResponseDTO>> getFailedPayments() {
        List<PaymentResponseDTO> response = paymentService.getFailedPayments();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending-older-than")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<PaymentResponseDTO>> getPendingPaymentsOlderThan(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant cutoffTime) {
        List<PaymentResponseDTO> response = paymentService.getPendingPaymentsOlderThan(cutoffTime);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/providers")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<PaymentProvider>> getSupportedProviders() {
        List<PaymentProvider> providers = paymentService.getSupportedProviders();
        return ResponseEntity.ok(providers);
    }
}
