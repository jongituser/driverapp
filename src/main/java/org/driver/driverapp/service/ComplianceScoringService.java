package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.enums.AuditAction;
import org.driver.driverapp.enums.AuditEntityType;
import org.driver.driverapp.model.AuditLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ComplianceScoringService {
    
    private final AuditLogService auditLogService;
    
    // Compliance thresholds
    private static final BigDecimal EXCELLENT_THRESHOLD = BigDecimal.valueOf(90);
    private static final BigDecimal GOOD_THRESHOLD = BigDecimal.valueOf(80);
    private static final BigDecimal FAIR_THRESHOLD = BigDecimal.valueOf(70);
    private static final BigDecimal POOR_THRESHOLD = BigDecimal.valueOf(60);
    
    // Scoring weights
    private static final BigDecimal DELIVERY_COMPLETION_WEIGHT = BigDecimal.valueOf(0.4);
    private static final BigDecimal ON_TIME_DELIVERY_WEIGHT = BigDecimal.valueOf(0.3);
    private static final BigDecimal CUSTOMER_SATISFACTION_WEIGHT = BigDecimal.valueOf(0.2);
    private static final BigDecimal SAFETY_COMPLIANCE_WEIGHT = BigDecimal.valueOf(0.1);
    
    /**
     * Calculate driver compliance score based on audit logs
     */
    public BigDecimal calculateDriverComplianceScore(Long driverId, Instant startDate, Instant endDate) {
        log.info("Calculating compliance score for driver: {} from {} to {}", driverId, startDate, endDate);
        
        List<AuditEntityType> relevantEntityTypes = List.of(
                AuditEntityType.DELIVERY,
                AuditEntityType.DRIVER,
                AuditEntityType.PAYMENT
        );
        
        List<AuditLog> auditLogs = auditLogService.getRecentAuditLogsForCompliance(relevantEntityTypes, startDate, endDate);
        
        // Filter audit logs for this specific driver
        List<AuditLog> driverAuditLogs = auditLogs.stream()
                .filter(log -> isDriverRelated(log, driverId))
                .collect(Collectors.toList());
        
        return calculateScoreFromAuditLogs(driverAuditLogs, "DRIVER");
    }
    
    /**
     * Calculate partner compliance score based on audit logs
     */
    public BigDecimal calculatePartnerComplianceScore(Long partnerId, Instant startDate, Instant endDate) {
        log.info("Calculating compliance score for partner: {} from {} to {}", partnerId, startDate, endDate);
        
        List<AuditEntityType> relevantEntityTypes = List.of(
                AuditEntityType.DELIVERY,
                AuditEntityType.PARTNER,
                AuditEntityType.INVENTORY_ITEM,
                AuditEntityType.PRODUCT,
                AuditEntityType.PAYMENT
        );
        
        List<AuditLog> auditLogs = auditLogService.getRecentAuditLogsForCompliance(relevantEntityTypes, startDate, endDate);
        
        // Filter audit logs for this specific partner
        List<AuditLog> partnerAuditLogs = auditLogs.stream()
                .filter(log -> isPartnerRelated(log, partnerId))
                .collect(Collectors.toList());
        
        return calculateScoreFromAuditLogs(partnerAuditLogs, "PARTNER");
    }
    
    /**
     * Get compliance status based on score
     */
    public String getComplianceStatus(BigDecimal score) {
        if (score.compareTo(EXCELLENT_THRESHOLD) >= 0) {
            return "EXCELLENT";
        } else if (score.compareTo(GOOD_THRESHOLD) >= 0) {
            return "GOOD";
        } else if (score.compareTo(FAIR_THRESHOLD) >= 0) {
            return "FAIR";
        } else if (score.compareTo(POOR_THRESHOLD) >= 0) {
            return "POOR";
        } else {
            return "CRITICAL";
        }
    }
    
    /**
     * Calculate overall system compliance score
     */
    public BigDecimal calculateSystemComplianceScore(Instant startDate, Instant endDate) {
        log.info("Calculating overall system compliance score from {} to {}", startDate, endDate);
        
        List<AuditEntityType> allEntityTypes = List.of(AuditEntityType.values());
        List<AuditLog> allAuditLogs = auditLogService.getRecentAuditLogsForCompliance(allEntityTypes, startDate, endDate);
        
        return calculateScoreFromAuditLogs(allAuditLogs, "SYSTEM");
    }
    
    /**
     * Get compliance trends over time
     */
    public Map<String, BigDecimal> getComplianceTrends(Long entityId, String entityType, int days) {
        Instant endDate = Instant.now();
        Instant startDate = endDate.minus(days, ChronoUnit.DAYS);
        
        BigDecimal currentScore;
        if ("DRIVER".equals(entityType)) {
            currentScore = calculateDriverComplianceScore(entityId, startDate, endDate);
        } else if ("PARTNER".equals(entityType)) {
            currentScore = calculatePartnerComplianceScore(entityId, startDate, endDate);
        } else {
            throw new IllegalArgumentException("Unsupported entity type: " + entityType);
        }
        
        // Calculate previous period score for comparison
        Instant previousEndDate = startDate;
        Instant previousStartDate = previousEndDate.minus(days, ChronoUnit.DAYS);
        
        BigDecimal previousScore;
        if ("DRIVER".equals(entityType)) {
            previousScore = calculateDriverComplianceScore(entityId, previousStartDate, previousEndDate);
        } else {
            previousScore = calculatePartnerComplianceScore(entityId, previousStartDate, previousEndDate);
        }
        
        return Map.of(
                "currentScore", currentScore,
                "previousScore", previousScore,
                "trend", currentScore.subtract(previousScore)
        );
    }
    
    /**
     * Check if audit log is related to a specific driver
     */
    private boolean isDriverRelated(AuditLog auditLog, Long driverId) {
        if (AuditEntityType.DRIVER.equals(auditLog.getEntityType()) && driverId.equals(auditLog.getEntityId())) {
            return true;
        }
        
        // Check if delivery is assigned to this driver
        if (AuditEntityType.DELIVERY.equals(auditLog.getEntityType())) {
            // This would require additional logic to check if the delivery is assigned to the driver
            // For now, we'll use a simple approach
            return true;
        }
        
        return false;
    }
    
    /**
     * Check if audit log is related to a specific partner
     */
    private boolean isPartnerRelated(AuditLog auditLog, Long partnerId) {
        if (AuditEntityType.PARTNER.equals(auditLog.getEntityType()) && partnerId.equals(auditLog.getEntityId())) {
            return true;
        }
        
        // Check if inventory item, product, or delivery belongs to this partner
        if (List.of(AuditEntityType.INVENTORY_ITEM, AuditEntityType.PRODUCT, AuditEntityType.DELIVERY)
                .contains(auditLog.getEntityType())) {
            // This would require additional logic to check if the entity belongs to the partner
            // For now, we'll use a simple approach
            return true;
        }
        
        return false;
    }
    
    /**
     * Calculate score from audit logs based on actions and patterns
     */
    private BigDecimal calculateScoreFromAuditLogs(List<AuditLog> auditLogs, String entityType) {
        if (auditLogs.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalScore = BigDecimal.ZERO;
        int totalActions = 0;
        
        // Group audit logs by action type
        Map<AuditAction, Long> actionCounts = auditLogs.stream()
                .collect(Collectors.groupingBy(AuditLog::getAction, Collectors.counting()));
        
        // Calculate positive actions (compliance)
        BigDecimal positiveScore = calculatePositiveActionsScore(actionCounts);
        
        // Calculate negative actions (non-compliance)
        BigDecimal negativeScore = calculateNegativeActionsScore(actionCounts);
        
        // Calculate base score
        totalScore = positiveScore.subtract(negativeScore);
        totalActions = auditLogs.size();
        
        // Normalize score to 0-100 range
        if (totalActions > 0) {
            BigDecimal normalizedScore = totalScore.divide(BigDecimal.valueOf(totalActions), 2, RoundingMode.HALF_UP);
            normalizedScore = normalizedScore.multiply(BigDecimal.valueOf(100));
            
            // Ensure score is within 0-100 range
            if (normalizedScore.compareTo(BigDecimal.ZERO) < 0) {
                normalizedScore = BigDecimal.ZERO;
            } else if (normalizedScore.compareTo(BigDecimal.valueOf(100)) > 0) {
                normalizedScore = BigDecimal.valueOf(100);
            }
            
            return normalizedScore.setScale(2, RoundingMode.HALF_UP);
        }
        
        return BigDecimal.ZERO;
    }
    
    /**
     * Calculate score for positive compliance actions
     */
    private BigDecimal calculatePositiveActionsScore(Map<AuditAction, Long> actionCounts) {
        BigDecimal score = BigDecimal.ZERO;
        
        // Positive actions that improve compliance
        Map<AuditAction, BigDecimal> positiveWeights = Map.of(
                AuditAction.COMPLETE, BigDecimal.valueOf(10),
                AuditAction.VERIFY, BigDecimal.valueOf(5),
                AuditAction.ACTIVATE, BigDecimal.valueOf(3),
                AuditAction.PAYMENT_COMPLETED, BigDecimal.valueOf(8),
                AuditAction.LOGIN, BigDecimal.valueOf(1)
        );
        
        for (Map.Entry<AuditAction, Long> entry : actionCounts.entrySet()) {
            if (positiveWeights.containsKey(entry.getKey())) {
                BigDecimal weight = positiveWeights.get(entry.getKey());
                BigDecimal count = BigDecimal.valueOf(entry.getValue());
                score = score.add(weight.multiply(count));
            }
        }
        
        return score;
    }
    
    /**
     * Calculate score for negative compliance actions
     */
    private BigDecimal calculateNegativeActionsScore(Map<AuditAction, Long> actionCounts) {
        BigDecimal score = BigDecimal.ZERO;
        
        // Negative actions that reduce compliance
        Map<AuditAction, BigDecimal> negativeWeights = Map.of(
                AuditAction.DELETE, BigDecimal.valueOf(15),
                AuditAction.DEACTIVATE, BigDecimal.valueOf(10),
                AuditAction.UNVERIFY, BigDecimal.valueOf(8),
                AuditAction.CANCEL, BigDecimal.valueOf(12),
                AuditAction.PAYMENT_FAILED, BigDecimal.valueOf(20),
                AuditAction.LOGOUT, BigDecimal.valueOf(1)
        );
        
        for (Map.Entry<AuditAction, Long> entry : actionCounts.entrySet()) {
            if (negativeWeights.containsKey(entry.getKey())) {
                BigDecimal weight = negativeWeights.get(entry.getKey());
                BigDecimal count = BigDecimal.valueOf(entry.getValue());
                score = score.add(weight.multiply(count));
            }
        }
        
        return score;
    }
}

