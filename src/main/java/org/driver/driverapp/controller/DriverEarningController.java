package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.driver.request.ProcessPayoutRequestDTO;
import org.driver.driverapp.dto.driver.response.DriverEarningResponseDTO;
import org.driver.driverapp.enums.PayoutStatus;
import org.driver.driverapp.service.DriverEarningService;
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
@RequestMapping("/api/v1/driver-earnings")
@RequiredArgsConstructor
public class DriverEarningController {

    private final DriverEarningService driverEarningService;

    @PostMapping("/delivery/{deliveryId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<DriverEarningResponseDTO> createEarningFromDelivery(@PathVariable Long deliveryId) {
        log.info("Creating earning from delivery: {}", deliveryId);
        
        DriverEarningResponseDTO response = driverEarningService.createEarningFromDelivery(deliveryId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/payout")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<DriverEarningResponseDTO> processPayout(@Valid @RequestBody ProcessPayoutRequestDTO requestDTO) {
        log.info("Processing payout for driver: {}", requestDTO.getDriverId());
        
        DriverEarningResponseDTO response = driverEarningService.processPayout(requestDTO.getDriverId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payout/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> processAllPendingPayouts() {
        log.info("Processing all pending payouts");
        
        driverEarningService.processAllPendingPayouts();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<DriverEarningResponseDTO> getEarningById(@PathVariable Long id) {
        DriverEarningResponseDTO response = driverEarningService.getEarningById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<DriverEarningResponseDTO>> getEarningsByDriver(
            @PathVariable Long driverId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<DriverEarningResponseDTO> response = driverEarningService.getEarningsByDriver(driverId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/delivery/{deliveryId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<DriverEarningResponseDTO> getEarningByDelivery(@PathVariable Long deliveryId) {
        DriverEarningResponseDTO response = driverEarningService.getEarningByDelivery(deliveryId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{payoutStatus}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Page<DriverEarningResponseDTO>> getEarningsByPayoutStatus(
            @PathVariable PayoutStatus payoutStatus,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<DriverEarningResponseDTO> response = driverEarningService.getEarningsByPayoutStatus(payoutStatus, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<DriverEarningResponseDTO>> getPendingPayouts() {
        List<DriverEarningResponseDTO> response = driverEarningService.getPendingPayouts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/failed")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<DriverEarningResponseDTO>> getFailedPayouts() {
        List<DriverEarningResponseDTO> response = driverEarningService.getFailedPayouts();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/driver/{driverId}/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getTotalEarningsByDriver(@PathVariable Long driverId) {
        BigDecimal total = driverEarningService.getTotalEarningsByDriver(driverId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/driver/{driverId}/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getPendingEarningsByDriver(@PathVariable Long driverId) {
        BigDecimal pending = driverEarningService.getPendingEarningsByDriver(driverId);
        return ResponseEntity.ok(pending);
    }

    @GetMapping("/status/{payoutStatus}/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<BigDecimal> getTotalEarningsByPayoutStatus(@PathVariable PayoutStatus payoutStatus) {
        BigDecimal total = driverEarningService.getTotalEarningsByPayoutStatus(payoutStatus);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<DriverEarningResponseDTO>> getEarningsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        List<DriverEarningResponseDTO> response = driverEarningService.getEarningsByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/driver/{driverId}/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<DriverEarningResponseDTO>> getEarningsByDriverAndDateRange(
            @PathVariable Long driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        List<DriverEarningResponseDTO> response = driverEarningService.getEarningsByDriverAndDateRange(driverId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
