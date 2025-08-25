package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.audit.request.CreateAuditLogRequestDTO;
import org.driver.driverapp.dto.audit.response.AuditLogResponseDTO;
import org.driver.driverapp.enums.AuditAction;
import org.driver.driverapp.enums.AuditEntityType;
import org.driver.driverapp.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Slf4j
public class AuditController {
    
    private final AuditLogService auditLogService;
    
    /**
     * Create a new audit log entry
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuditLogResponseDTO> createAuditLog(@Valid @RequestBody CreateAuditLogRequestDTO request) {
        log.info("Creating audit log: entityType={}, entityId={}, action={}", 
                request.getEntityType(), request.getEntityId(), request.getAction());
        
        AuditLogResponseDTO response = auditLogService.createAuditLog(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get audit logs by entity type and ID
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponseDTO>> getAuditLogsByEntity(
            @PathVariable AuditEntityType entityType,
            @PathVariable Long entityId) {
        
        log.info("Getting audit logs for entity: type={}, id={}", entityType, entityId);
        
        List<AuditLogResponseDTO> auditLogs = auditLogService.getAuditLogsByEntity(entityType, entityId);
        return ResponseEntity.ok(auditLogs);
    }
    
    /**
     * Get audit logs by user
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponseDTO>> getAuditLogsByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        
        log.info("Getting audit logs for user: userId={}", userId);
        
        Page<AuditLogResponseDTO> auditLogs = auditLogService.getAuditLogsByUser(userId, pageable);
        return ResponseEntity.ok(auditLogs);
    }
    
    /**
     * Get audit logs by action
     */
    @GetMapping("/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponseDTO>> getAuditLogsByAction(
            @PathVariable AuditAction action,
            Pageable pageable) {
        
        log.info("Getting audit logs for action: action={}", action);
        
        Page<AuditLogResponseDTO> auditLogs = auditLogService.getAuditLogsByAction(action, pageable);
        return ResponseEntity.ok(auditLogs);
    }
    
    /**
     * Get audit logs by entity type
     */
    @GetMapping("/entity-type/{entityType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponseDTO>> getAuditLogsByEntityType(
            @PathVariable AuditEntityType entityType,
            Pageable pageable) {
        
        log.info("Getting audit logs for entity type: entityType={}", entityType);
        
        Page<AuditLogResponseDTO> auditLogs = auditLogService.getAuditLogsByEntityType(entityType, pageable);
        return ResponseEntity.ok(auditLogs);
    }
    
    /**
     * Get audit logs by date range
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogResponseDTO>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            Pageable pageable) {
        
        log.info("Getting audit logs by date range: startDate={}, endDate={}", startDate, endDate);
        
        Page<AuditLogResponseDTO> auditLogs = auditLogService.getAuditLogsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(auditLogs);
    }
    
    /**
     * Get audit log by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuditLogResponseDTO> getAuditLogById(@PathVariable Long id) {
        log.info("Getting audit log by ID: id={}", id);
        
        return auditLogService.getAuditLogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get audit log statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getAuditLogStats() {
        log.info("Getting audit log statistics");
        
        // This would return various statistics about audit logs
        // For now, returning a simple response
        return ResponseEntity.ok(Map.of(
                "message", "Audit log statistics endpoint",
                "status", "implemented"
        ));
    }
}

