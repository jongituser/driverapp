package org.driver.driverapp.service;

import org.driver.driverapp.enums.AuditAction;
import org.driver.driverapp.enums.AuditEntityType;
import org.driver.driverapp.model.AuditLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplianceScoringServiceTest {
    
    @Mock
    private AuditLogService auditLogService;
    
    @InjectMocks
    private ComplianceScoringService complianceScoringService;
    
    private AuditLog positiveAuditLog;
    private AuditLog negativeAuditLog;
    private Instant startDate;
    private Instant endDate;
    
    @BeforeEach
    void setUp() {
        startDate = Instant.now().minusSeconds(3600);
        endDate = Instant.now();
        
        positiveAuditLog = AuditLog.builder()
                .id(1L)
                .entityType(AuditEntityType.DELIVERY)
                .entityId(100L)
                .action(AuditAction.COMPLETE)
                .userId(1L)
                .userEmail("driver@example.com")
                .active(true)
                .createdAt(Instant.now())
                .build();
        
        negativeAuditLog = AuditLog.builder()
                .id(2L)
                .entityType(AuditEntityType.DELIVERY)
                .entityId(101L)
                .action(AuditAction.CANCEL)
                .userId(1L)
                .userEmail("driver@example.com")
                .active(true)
                .createdAt(Instant.now())
                .build();
    }
    
    @Test
    void calculateDriverComplianceScore_Success() {
        // Given
        Long driverId = 1L;
        List<AuditLog> auditLogs = Arrays.asList(positiveAuditLog, negativeAuditLog);
        
        when(auditLogService.getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate)))
                .thenReturn(auditLogs);
        
        // When
        BigDecimal result = complianceScoringService.calculateDriverComplianceScore(driverId, startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(result.compareTo(BigDecimal.valueOf(100)) <= 0);
        
        verify(auditLogService).getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate));
    }
    
    @Test
    void calculateDriverComplianceScore_EmptyAuditLogs() {
        // Given
        Long driverId = 1L;
        List<AuditLog> auditLogs = Arrays.asList();
        
        when(auditLogService.getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate)))
                .thenReturn(auditLogs);
        
        // When
        BigDecimal result = complianceScoringService.calculateDriverComplianceScore(driverId, startDate, endDate);
        
        // Then
        assertEquals(BigDecimal.ZERO, result);
        
        verify(auditLogService).getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate));
    }
    
    @Test
    void calculatePartnerComplianceScore_Success() {
        // Given
        Long partnerId = 1L;
        List<AuditLog> auditLogs = Arrays.asList(positiveAuditLog, negativeAuditLog);
        
        when(auditLogService.getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate)))
                .thenReturn(auditLogs);
        
        // When
        BigDecimal result = complianceScoringService.calculatePartnerComplianceScore(partnerId, startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(result.compareTo(BigDecimal.valueOf(100)) <= 0);
        
        verify(auditLogService).getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate));
    }
    
    @Test
    void getComplianceStatus_Excellent() {
        // Given
        BigDecimal score = BigDecimal.valueOf(95);
        
        // When
        String result = complianceScoringService.getComplianceStatus(score);
        
        // Then
        assertEquals("EXCELLENT", result);
    }
    
    @Test
    void getComplianceStatus_Good() {
        // Given
        BigDecimal score = BigDecimal.valueOf(85);
        
        // When
        String result = complianceScoringService.getComplianceStatus(score);
        
        // Then
        assertEquals("GOOD", result);
    }
    
    @Test
    void getComplianceStatus_Fair() {
        // Given
        BigDecimal score = BigDecimal.valueOf(75);
        
        // When
        String result = complianceScoringService.getComplianceStatus(score);
        
        // Then
        assertEquals("FAIR", result);
    }
    
    @Test
    void getComplianceStatus_Poor() {
        // Given
        BigDecimal score = BigDecimal.valueOf(65);
        
        // When
        String result = complianceScoringService.getComplianceStatus(score);
        
        // Then
        assertEquals("POOR", result);
    }
    
    @Test
    void getComplianceStatus_Critical() {
        // Given
        BigDecimal score = BigDecimal.valueOf(55);
        
        // When
        String result = complianceScoringService.getComplianceStatus(score);
        
        // Then
        assertEquals("CRITICAL", result);
    }
    
    @Test
    void calculateSystemComplianceScore_Success() {
        // Given
        List<AuditLog> auditLogs = Arrays.asList(positiveAuditLog, negativeAuditLog);
        
        when(auditLogService.getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate)))
                .thenReturn(auditLogs);
        
        // When
        BigDecimal result = complianceScoringService.calculateSystemComplianceScore(startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(result.compareTo(BigDecimal.valueOf(100)) <= 0);
        
        verify(auditLogService).getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate));
    }
    
    @Test
    void getComplianceTrends_Driver_Success() {
        // Given
        Long driverId = 1L;
        String entityType = "DRIVER";
        int days = 30;
        List<AuditLog> currentAuditLogs = Arrays.asList(positiveAuditLog);
        List<AuditLog> previousAuditLogs = Arrays.asList(negativeAuditLog);
        
        when(auditLogService.getRecentAuditLogsForCompliance(anyList(), any(Instant.class), any(Instant.class)))
                .thenReturn(currentAuditLogs)
                .thenReturn(previousAuditLogs);
        
        // When
        Map<String, BigDecimal> result = complianceScoringService.getComplianceTrends(driverId, entityType, days);
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("currentScore"));
        assertTrue(result.containsKey("previousScore"));
        assertTrue(result.containsKey("trend"));
        
        verify(auditLogService, times(2)).getRecentAuditLogsForCompliance(anyList(), any(Instant.class), any(Instant.class));
    }
    
    @Test
    void getComplianceTrends_Partner_Success() {
        // Given
        Long partnerId = 1L;
        String entityType = "PARTNER";
        int days = 30;
        List<AuditLog> currentAuditLogs = Arrays.asList(positiveAuditLog);
        List<AuditLog> previousAuditLogs = Arrays.asList(negativeAuditLog);
        
        when(auditLogService.getRecentAuditLogsForCompliance(anyList(), any(Instant.class), any(Instant.class)))
                .thenReturn(currentAuditLogs)
                .thenReturn(previousAuditLogs);
        
        // When
        Map<String, BigDecimal> result = complianceScoringService.getComplianceTrends(partnerId, entityType, days);
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("currentScore"));
        assertTrue(result.containsKey("previousScore"));
        assertTrue(result.containsKey("trend"));
        
        verify(auditLogService, times(2)).getRecentAuditLogsForCompliance(anyList(), any(Instant.class), any(Instant.class));
    }
    
    @Test
    void getComplianceTrends_InvalidEntityType() {
        // Given
        Long entityId = 1L;
        String entityType = "INVALID";
        int days = 30;
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
                complianceScoringService.getComplianceTrends(entityId, entityType, days));
        
        verify(auditLogService, never()).getRecentAuditLogsForCompliance(anyList(), any(Instant.class), any(Instant.class));
    }
    
    @Test
    void calculateScoreFromAuditLogs_PositiveActions() {
        // Given
        Long driverId = 1L;
        AuditLog completeLog = AuditLog.builder()
                .id(1L)
                .entityType(AuditEntityType.DELIVERY)
                .entityId(100L)
                .action(AuditAction.COMPLETE)
                .active(true)
                .createdAt(Instant.now())
                .build();
        
        List<AuditLog> auditLogs = Arrays.asList(completeLog);
        
        when(auditLogService.getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate)))
                .thenReturn(auditLogs);
        
        // When
        BigDecimal result = complianceScoringService.calculateDriverComplianceScore(driverId, startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
        
        verify(auditLogService).getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate));
    }
    
    @Test
    void calculateScoreFromAuditLogs_NegativeActions() {
        // Given
        Long driverId = 1L;
        AuditLog cancelLog = AuditLog.builder()
                .id(1L)
                .entityType(AuditEntityType.DELIVERY)
                .entityId(100L)
                .action(AuditAction.CANCEL)
                .active(true)
                .createdAt(Instant.now())
                .build();
        
        List<AuditLog> auditLogs = Arrays.asList(cancelLog);
        
        when(auditLogService.getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate)))
                .thenReturn(auditLogs);
        
        // When
        BigDecimal result = complianceScoringService.calculateDriverComplianceScore(driverId, startDate, endDate);
        
        // Then
        assertNotNull(result);
        // Score should be lower due to negative action
        
        verify(auditLogService).getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate));
    }
    
    @Test
    void calculateScoreFromAuditLogs_MixedActions() {
        // Given
        Long driverId = 1L;
        AuditLog completeLog = AuditLog.builder()
                .id(1L)
                .entityType(AuditEntityType.DELIVERY)
                .entityId(100L)
                .action(AuditAction.COMPLETE)
                .active(true)
                .createdAt(Instant.now())
                .build();
        
        AuditLog cancelLog = AuditLog.builder()
                .id(2L)
                .entityType(AuditEntityType.DELIVERY)
                .entityId(101L)
                .action(AuditAction.CANCEL)
                .active(true)
                .createdAt(Instant.now())
                .build();
        
        List<AuditLog> auditLogs = Arrays.asList(completeLog, cancelLog);
        
        when(auditLogService.getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate)))
                .thenReturn(auditLogs);
        
        // When
        BigDecimal result = complianceScoringService.calculateDriverComplianceScore(driverId, startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(result.compareTo(BigDecimal.valueOf(100)) <= 0);
        
        verify(auditLogService).getRecentAuditLogsForCompliance(anyList(), eq(startDate), eq(endDate));
    }
}

