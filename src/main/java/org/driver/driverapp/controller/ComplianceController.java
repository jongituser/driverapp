package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.service.ComplianceScoringService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/compliance")
@RequiredArgsConstructor
@Slf4j
public class ComplianceController {
    
    private final ComplianceScoringService complianceScoringService;
    
    /**
     * Get driver compliance score
     */
    @GetMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    public ResponseEntity<Map<String, Object>> getDriverComplianceScore(
            @PathVariable Long driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        
        log.info("Getting compliance score for driver: driverId={}, startDate={}, endDate={}", 
                driverId, startDate, endDate);
        
        BigDecimal score = complianceScoringService.calculateDriverComplianceScore(driverId, startDate, endDate);
        String status = complianceScoringService.getComplianceStatus(score);
        
        Map<String, Object> response = Map.of(
                "driverId", driverId,
                "score", score,
                "status", status,
                "startDate", startDate,
                "endDate", endDate
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get partner compliance score
     */
    @GetMapping("/partner/{partnerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPartnerComplianceScore(
            @PathVariable Long partnerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        
        log.info("Getting compliance score for partner: partnerId={}, startDate={}, endDate={}", 
                partnerId, startDate, endDate);
        
        BigDecimal score = complianceScoringService.calculatePartnerComplianceScore(partnerId, startDate, endDate);
        String status = complianceScoringService.getComplianceStatus(score);
        
        Map<String, Object> response = Map.of(
                "partnerId", partnerId,
                "score", score,
                "status", status,
                "startDate", startDate,
                "endDate", endDate
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get system compliance score
     */
    @GetMapping("/system")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSystemComplianceScore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {
        
        log.info("Getting system compliance score: startDate={}, endDate={}", startDate, endDate);
        
        BigDecimal score = complianceScoringService.calculateSystemComplianceScore(startDate, endDate);
        String status = complianceScoringService.getComplianceStatus(score);
        
        Map<String, Object> response = Map.of(
                "score", score,
                "status", status,
                "startDate", startDate,
                "endDate", endDate
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get compliance trends
     */
    @GetMapping("/trends/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    public ResponseEntity<Map<String, BigDecimal>> getComplianceTrends(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            @RequestParam(defaultValue = "30") int days) {
        
        log.info("Getting compliance trends for {} {}: days={}", entityType, entityId, days);
        
        Map<String, BigDecimal> trends = complianceScoringService.getComplianceTrends(entityId, entityType, days);
        return ResponseEntity.ok(trends);
    }
    
    /**
     * Get compliance status thresholds
     */
    @GetMapping("/thresholds")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARTNER')")
    public ResponseEntity<Map<String, Object>> getComplianceThresholds() {
        log.info("Getting compliance thresholds");
        
        Map<String, Object> thresholds = Map.of(
                "excellent", 90,
                "good", 80,
                "fair", 70,
                "poor", 60,
                "critical", 0
        );
        
        return ResponseEntity.ok(thresholds);
    }
}

