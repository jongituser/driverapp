package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.analytics.request.CreateAnalyticsRecordRequestDTO;
import org.driver.driverapp.dto.analytics.response.*;
import org.driver.driverapp.enums.AnalyticsRecordType;
import org.driver.driverapp.service.AnalyticsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    // Analytics Record Management
    @PostMapping("/records")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalyticsRecordResponseDTO> createAnalyticsRecord(
            @Valid @RequestBody CreateAnalyticsRecordRequestDTO request) {
        log.info("Creating analytics record: type={}, entityId={}", request.getType(), request.getEntityId());
        AnalyticsRecordResponseDTO response = analyticsService.createAnalyticsRecord(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/records")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AnalyticsRecordResponseDTO>> getAnalyticsRecords(
            @RequestParam(required = false) AnalyticsRecordType type,
            Pageable pageable) {
        log.info("Getting analytics records: type={}", type);
        Page<AnalyticsRecordResponseDTO> response = analyticsService.getAnalyticsRecords(type, pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/records/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalyticsRecordResponseDTO> getAnalyticsRecord(@PathVariable Long id) {
        log.info("Getting analytics record: id={}", id);
        AnalyticsRecordResponseDTO response = analyticsService.getAnalyticsRecord(id);
        return ResponseEntity.ok(response);
    }
    
    // Admin Dashboard Analytics
    @GetMapping("/admin/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminAnalyticsSummaryDTO> getAdminAnalyticsSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        LocalDate startDate = fromDate != null ? fromDate : LocalDate.now().minusDays(30);
        LocalDate endDate = toDate != null ? toDate : LocalDate.now();
        
        log.info("Getting admin analytics summary from {} to {}", startDate, endDate);
        AdminAnalyticsSummaryDTO response = analyticsService.getAdminAnalyticsSummary(startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    // Partner Dashboard Analytics
    @GetMapping("/partner/{partnerId}/summary")
    @PreAuthorize("hasRole('PARTNER') and #partnerId == authentication.principal.partnerId or hasRole('ADMIN')")
    public ResponseEntity<PartnerAnalyticsSummaryDTO> getPartnerAnalyticsSummary(
            @PathVariable Long partnerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        LocalDate startDate = fromDate != null ? fromDate : LocalDate.now().minusDays(30);
        LocalDate endDate = toDate != null ? toDate : LocalDate.now();
        
        log.info("Getting partner analytics summary for partnerId={} from {} to {}", partnerId, startDate, endDate);
        PartnerAnalyticsSummaryDTO response = analyticsService.getPartnerAnalyticsSummary(partnerId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    // Geospatial Analytics
    @GetMapping("/geospatial")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GeospatialAnalyticsDTO> getGeospatialAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        LocalDate startDate = fromDate != null ? fromDate : LocalDate.now().minusDays(30);
        LocalDate endDate = toDate != null ? toDate : LocalDate.now();
        
        log.info("Getting geospatial analytics from {} to {}", startDate, endDate);
        GeospatialAnalyticsDTO response = analyticsService.getGeospatialAnalytics(startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    // Compliance Report
    @GetMapping("/compliance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComplianceReportDTO> getComplianceReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        LocalDate startDate = fromDate != null ? fromDate : LocalDate.now().minusDays(30);
        LocalDate endDate = toDate != null ? toDate : LocalDate.now();
        
        log.info("Getting compliance report from {} to {}", startDate, endDate);
        ComplianceReportDTO response = analyticsService.getComplianceReport(startDate, endDate);
        return ResponseEntity.ok(response);
    }
}

