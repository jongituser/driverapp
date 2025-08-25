package org.driver.driverapp.dto.audit.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.AuditAction;
import org.driver.driverapp.enums.AuditEntityType;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDTO {
    
    private Long id;
    private AuditEntityType entityType;
    private Long entityId;
    private AuditAction action;
    private Long userId;
    private String userEmail;
    private String beforeSnapshot;
    private String afterSnapshot;
    private String changesSummary;
    private String ipAddress;
    private String userAgent;
    private boolean active;
    private Long version;
    private Instant createdAt;
}

