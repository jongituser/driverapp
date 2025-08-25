package org.driver.driverapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.driver.driverapp.dto.audit.request.CreateAuditLogRequestDTO;
import org.driver.driverapp.dto.audit.response.AuditLogResponseDTO;
import org.driver.driverapp.enums.AuditAction;
import org.driver.driverapp.enums.AuditEntityType;
import org.driver.driverapp.mapper.AuditLogMapper;
import org.driver.driverapp.model.AuditLog;
import org.driver.driverapp.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {
    
    @Mock
    private AuditLogRepository auditLogRepository;
    
    @Mock
    private AuditLogMapper auditLogMapper;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @InjectMocks
    private AuditLogService auditLogService;
    
    private AuditLog auditLog;
    private CreateAuditLogRequestDTO createRequest;
    private AuditLogResponseDTO responseDTO;
    
    @BeforeEach
    void setUp() {
        auditLog = AuditLog.builder()
                .id(1L)
                .entityType(AuditEntityType.DELIVERY)
                .entityId(100L)
                .action(AuditAction.CREATE)
                .userId(1L)
                .userEmail("test@example.com")
                .beforeSnapshot("{\"status\":\"PENDING\"}")
                .afterSnapshot("{\"status\":\"ASSIGNED\"}")
                .changesSummary("Delivery status updated from PENDING to ASSIGNED")
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .active(true)
                .version(0L)
                .createdAt(Instant.now())
                .build();
        
        createRequest = CreateAuditLogRequestDTO.builder()
                .entityType(AuditEntityType.DELIVERY)
                .entityId(100L)
                .action(AuditAction.CREATE)
                .userId(1L)
                .userEmail("test@example.com")
                .beforeSnapshot("{\"status\":\"PENDING\"}")
                .afterSnapshot("{\"status\":\"ASSIGNED\"}")
                .changesSummary("Delivery status updated from PENDING to ASSIGNED")
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .build();
        
        responseDTO = AuditLogResponseDTO.builder()
                .id(1L)
                .entityType(AuditEntityType.DELIVERY)
                .entityId(100L)
                .action(AuditAction.CREATE)
                .userId(1L)
                .userEmail("test@example.com")
                .beforeSnapshot("{\"status\":\"PENDING\"}")
                .afterSnapshot("{\"status\":\"ASSIGNED\"}")
                .changesSummary("Delivery status updated from PENDING to ASSIGNED")
                .ipAddress("192.168.1.1")
                .userAgent("Mozilla/5.0")
                .active(true)
                .version(0L)
                .createdAt(Instant.now())
                .build();
    }
    
    @Test
    void createAuditLog_Success() {
        // Given
        when(auditLogMapper.toEntity(createRequest)).thenReturn(auditLog);
        when(auditLogRepository.save(auditLog)).thenReturn(auditLog);
        when(auditLogMapper.toResponseDTO(auditLog)).thenReturn(responseDTO);
        
        // When
        AuditLogResponseDTO result = auditLogService.createAuditLog(createRequest);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(AuditEntityType.DELIVERY, result.getEntityType());
        assertEquals(100L, result.getEntityId());
        assertEquals(AuditAction.CREATE, result.getAction());
        
        verify(auditLogMapper).toEntity(createRequest);
        verify(auditLogRepository).save(auditLog);
        verify(auditLogMapper).toResponseDTO(auditLog);
    }
    
    @Test
    void createAuditLogWithSnapshots_Success() throws Exception {
        // Given
        Object beforeSnapshot = Map.of("status", "PENDING");
        Object afterSnapshot = Map.of("status", "ASSIGNED");
        
        when(objectMapper.writeValueAsString(beforeSnapshot)).thenReturn("{\"status\":\"PENDING\"}");
        when(objectMapper.writeValueAsString(afterSnapshot)).thenReturn("{\"status\":\"ASSIGNED\"}");
        when(auditLogMapper.toEntity(any(CreateAuditLogRequestDTO.class))).thenReturn(auditLog);
        when(auditLogRepository.save(auditLog)).thenReturn(auditLog);
        when(auditLogMapper.toResponseDTO(auditLog)).thenReturn(responseDTO);
        
        // When
        AuditLogResponseDTO result = auditLogService.createAuditLogWithSnapshots(
                AuditEntityType.DELIVERY, 100L, AuditAction.UPDATE, 
                beforeSnapshot, afterSnapshot, 1L, "test@example.com");
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        
        verify(objectMapper).writeValueAsString(beforeSnapshot);
        verify(objectMapper).writeValueAsString(afterSnapshot);
        verify(auditLogRepository).save(auditLog);
    }
    
    @Test
    void createAuditLogWithSnapshots_NullSnapshots() throws Exception {
        // Given
        when(auditLogMapper.toEntity(any(CreateAuditLogRequestDTO.class))).thenReturn(auditLog);
        when(auditLogRepository.save(auditLog)).thenReturn(auditLog);
        when(auditLogMapper.toResponseDTO(auditLog)).thenReturn(responseDTO);
        
        // When
        AuditLogResponseDTO result = auditLogService.createAuditLogWithSnapshots(
                AuditEntityType.DELIVERY, 100L, AuditAction.CREATE, 
                null, null, 1L, "test@example.com");
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        
        verify(auditLogRepository).save(auditLog);
    }
    
    @Test
    void getAuditLogsByEntity_Success() {
        // Given
        List<AuditLog> auditLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByEntityTypeAndEntityIdAndActiveTrueOrderByCreatedAtDesc(
                AuditEntityType.DELIVERY, 100L)).thenReturn(auditLogs);
        when(auditLogMapper.toResponseDTO(auditLog)).thenReturn(responseDTO);
        
        // When
        List<AuditLogResponseDTO> result = auditLogService.getAuditLogsByEntity(AuditEntityType.DELIVERY, 100L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        
        verify(auditLogRepository).findByEntityTypeAndEntityIdAndActiveTrueOrderByCreatedAtDesc(
                AuditEntityType.DELIVERY, 100L);
        verify(auditLogMapper).toResponseDTO(auditLog);
    }
    
    @Test
    void getAuditLogsByUser_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(auditLog));
        
        when(auditLogRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(1L, pageable)).thenReturn(page);
        when(auditLogMapper.toResponseDTO(auditLog)).thenReturn(responseDTO);
        
        // When
        Page<AuditLogResponseDTO> result = auditLogService.getAuditLogsByUser(1L, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        
        verify(auditLogRepository).findByUserIdAndActiveTrueOrderByCreatedAtDesc(1L, pageable);
        verify(auditLogMapper).toResponseDTO(auditLog);
    }
    
    @Test
    void getAuditLogsByAction_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(auditLog));
        
        when(auditLogRepository.findByActionAndActiveTrueOrderByCreatedAtDesc(AuditAction.CREATE, pageable)).thenReturn(page);
        when(auditLogMapper.toResponseDTO(auditLog)).thenReturn(responseDTO);
        
        // When
        Page<AuditLogResponseDTO> result = auditLogService.getAuditLogsByAction(AuditAction.CREATE, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        
        verify(auditLogRepository).findByActionAndActiveTrueOrderByCreatedAtDesc(AuditAction.CREATE, pageable);
        verify(auditLogMapper).toResponseDTO(auditLog);
    }
    
    @Test
    void getAuditLogsByEntityType_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(auditLog));
        
        when(auditLogRepository.findByEntityTypeAndActiveTrueOrderByCreatedAtDesc(AuditEntityType.DELIVERY, pageable)).thenReturn(page);
        when(auditLogMapper.toResponseDTO(auditLog)).thenReturn(responseDTO);
        
        // When
        Page<AuditLogResponseDTO> result = auditLogService.getAuditLogsByEntityType(AuditEntityType.DELIVERY, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        
        verify(auditLogRepository).findByEntityTypeAndActiveTrueOrderByCreatedAtDesc(AuditEntityType.DELIVERY, pageable);
        verify(auditLogMapper).toResponseDTO(auditLog);
    }
    
    @Test
    void getAuditLogsByDateRange_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<AuditLog> page = new PageImpl<>(Arrays.asList(auditLog));
        Instant startDate = Instant.now().minusSeconds(3600);
        Instant endDate = Instant.now();
        
        when(auditLogRepository.findByCreatedAtBetween(startDate, endDate, pageable)).thenReturn(page);
        when(auditLogMapper.toResponseDTO(auditLog)).thenReturn(responseDTO);
        
        // When
        Page<AuditLogResponseDTO> result = auditLogService.getAuditLogsByDateRange(startDate, endDate, pageable);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        
        verify(auditLogRepository).findByCreatedAtBetween(startDate, endDate, pageable);
        verify(auditLogMapper).toResponseDTO(auditLog);
    }
    
    @Test
    void getAuditLogById_Success() {
        // Given
        when(auditLogRepository.findById(1L)).thenReturn(Optional.of(auditLog));
        when(auditLogMapper.toResponseDTO(auditLog)).thenReturn(responseDTO);
        
        // When
        Optional<AuditLogResponseDTO> result = auditLogService.getAuditLogById(1L);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        
        verify(auditLogRepository).findById(1L);
        verify(auditLogMapper).toResponseDTO(auditLog);
    }
    
    @Test
    void getAuditLogById_NotFound() {
        // Given
        when(auditLogRepository.findById(1L)).thenReturn(Optional.empty());
        
        // When
        Optional<AuditLogResponseDTO> result = auditLogService.getAuditLogById(1L);
        
        // Then
        assertFalse(result.isPresent());
        
        verify(auditLogRepository).findById(1L);
        verify(auditLogMapper, never()).toResponseDTO(any());
    }
    
    @Test
    void getRecentAuditLogsForCompliance_Success() {
        // Given
        List<AuditEntityType> entityTypes = Arrays.asList(AuditEntityType.DELIVERY, AuditEntityType.DRIVER);
        Instant startDate = Instant.now().minusSeconds(3600);
        Instant endDate = Instant.now();
        List<AuditLog> auditLogs = Arrays.asList(auditLog);
        
        when(auditLogRepository.findRecentAuditLogsForCompliance(entityTypes, startDate, endDate)).thenReturn(auditLogs);
        
        // When
        List<AuditLog> result = auditLogService.getRecentAuditLogsForCompliance(entityTypes, startDate, endDate);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        
        verify(auditLogRepository).findRecentAuditLogsForCompliance(entityTypes, startDate, endDate);
    }
    
    @Test
    void countByEntityTypeAndAction_Success() {
        // Given
        when(auditLogRepository.countByEntityTypeAndAction(AuditEntityType.DELIVERY, AuditAction.CREATE)).thenReturn(5L);
        
        // When
        Long result = auditLogService.countByEntityTypeAndAction(AuditEntityType.DELIVERY, AuditAction.CREATE);
        
        // Then
        assertEquals(5L, result);
        
        verify(auditLogRepository).countByEntityTypeAndAction(AuditEntityType.DELIVERY, AuditAction.CREATE);
    }
    
    @Test
    void countByUserIdAndAction_Success() {
        // Given
        when(auditLogRepository.countByUserIdAndAction(1L, AuditAction.CREATE)).thenReturn(3L);
        
        // When
        Long result = auditLogService.countByUserIdAndAction(1L, AuditAction.CREATE);
        
        // Then
        assertEquals(3L, result);
        
        verify(auditLogRepository).countByUserIdAndAction(1L, AuditAction.CREATE);
    }
}
