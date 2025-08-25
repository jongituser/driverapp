package org.driver.driverapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.audit.request.CreateAuditLogRequestDTO;
import org.driver.driverapp.dto.audit.response.AuditLogResponseDTO;
import org.driver.driverapp.enums.AuditAction;
import org.driver.driverapp.enums.AuditEntityType;
import org.driver.driverapp.mapper.AuditLogMapper;
import org.driver.driverapp.model.AuditLog;
import org.driver.driverapp.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;
    private final ObjectMapper objectMapper;
    
    /**
     * Create an audit log entry
     */
    public AuditLogResponseDTO createAuditLog(CreateAuditLogRequestDTO request) {
        log.info("Creating audit log: entityType={}, entityId={}, action={}", 
                request.getEntityType(), request.getEntityId(), request.getAction());
        
        AuditLog auditLog = auditLogMapper.toEntity(request);
        AuditLog savedAuditLog = auditLogRepository.save(auditLog);
        
        return auditLogMapper.toResponseDTO(savedAuditLog);
    }
    
    /**
     * Create audit log with before/after snapshots
     */
    public AuditLogResponseDTO createAuditLogWithSnapshots(AuditEntityType entityType, Long entityId, 
                                                          AuditAction action, Object beforeSnapshot, 
                                                          Object afterSnapshot, Long userId, String userEmail) {
        try {
            String beforeJson = beforeSnapshot != null ? objectMapper.writeValueAsString(beforeSnapshot) : null;
            String afterJson = afterSnapshot != null ? objectMapper.writeValueAsString(afterSnapshot) : null;
            
            CreateAuditLogRequestDTO request = CreateAuditLogRequestDTO.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .action(action)
                    .userId(userId)
                    .userEmail(userEmail)
                    .beforeSnapshot(beforeJson)
                    .afterSnapshot(afterJson)
                    .changesSummary(generateChangesSummary(beforeSnapshot, afterSnapshot))
                    .build();
            
            return createAuditLog(request);
        } catch (JsonProcessingException e) {
            log.error("Error serializing audit snapshots for entityType={}, entityId={}", entityType, entityId, e);
            throw new RuntimeException("Failed to create audit log", e);
        }
    }
    
    /**
     * Get audit logs by entity type and ID
     */
    public List<AuditLogResponseDTO> getAuditLogsByEntity(AuditEntityType entityType, Long entityId) {
        List<AuditLog> auditLogs = auditLogRepository.findByEntityTypeAndEntityIdAndActiveTrueOrderByCreatedAtDesc(entityType, entityId);
        return auditLogs.stream()
                .map(auditLogMapper::toResponseDTO)
                .toList();
    }
    
    /**
     * Get audit logs by user
     */
    public Page<AuditLogResponseDTO> getAuditLogsByUser(Long userId, Pageable pageable) {
        Page<AuditLog> auditLogs = auditLogRepository.findByUserIdAndActiveTrueOrderByCreatedAtDesc(userId, pageable);
        return auditLogs.map(auditLogMapper::toResponseDTO);
    }
    
    /**
     * Get audit logs by action
     */
    public Page<AuditLogResponseDTO> getAuditLogsByAction(AuditAction action, Pageable pageable) {
        Page<AuditLog> auditLogs = auditLogRepository.findByActionAndActiveTrueOrderByCreatedAtDesc(action, pageable);
        return auditLogs.map(auditLogMapper::toResponseDTO);
    }
    
    /**
     * Get audit logs by entity type
     */
    public Page<AuditLogResponseDTO> getAuditLogsByEntityType(AuditEntityType entityType, Pageable pageable) {
        Page<AuditLog> auditLogs = auditLogRepository.findByEntityTypeAndActiveTrueOrderByCreatedAtDesc(entityType, pageable);
        return auditLogs.map(auditLogMapper::toResponseDTO);
    }
    
    /**
     * Get audit logs by date range
     */
    public Page<AuditLogResponseDTO> getAuditLogsByDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        Page<AuditLog> auditLogs = auditLogRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        return auditLogs.map(auditLogMapper::toResponseDTO);
    }
    
    /**
     * Get audit log by ID
     */
    public Optional<AuditLogResponseDTO> getAuditLogById(Long id) {
        return auditLogRepository.findById(id)
                .map(auditLogMapper::toResponseDTO);
    }
    
    /**
     * Get recent audit logs for compliance scoring
     */
    public List<AuditLog> getRecentAuditLogsForCompliance(List<AuditEntityType> entityTypes, 
                                                         Instant startDate, Instant endDate) {
        return auditLogRepository.findRecentAuditLogsForCompliance(entityTypes, startDate, endDate);
    }
    
    /**
     * Count audit logs by entity type and action
     */
    public Long countByEntityTypeAndAction(AuditEntityType entityType, AuditAction action) {
        return auditLogRepository.countByEntityTypeAndAction(entityType, action);
    }
    
    /**
     * Count audit logs by user and action
     */
    public Long countByUserIdAndAction(Long userId, AuditAction action) {
        return auditLogRepository.countByUserIdAndAction(userId, action);
    }
    
    /**
     * Generate a summary of changes between before and after snapshots
     */
    private String generateChangesSummary(Object before, Object after) {
        if (before == null && after == null) {
            return "No changes";
        }
        
        if (before == null) {
            return "Entity created";
        }
        
        if (after == null) {
            return "Entity deleted";
        }
        
        // Simple change detection - in a real implementation, you might want to use a more sophisticated diff library
        return "Entity updated";
    }
}

