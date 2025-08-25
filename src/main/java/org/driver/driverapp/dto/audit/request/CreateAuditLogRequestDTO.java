package org.driver.driverapp.dto.audit.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.AuditAction;
import org.driver.driverapp.enums.AuditEntityType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuditLogRequestDTO {
    
    @NotNull(message = "Entity type is required")
    private AuditEntityType entityType;
    
    @NotNull(message = "Entity ID is required")
    private Long entityId;
    
    @NotNull(message = "Action is required")
    private AuditAction action;
    
    private Long userId;
    
    private String userEmail;
    
    private String beforeSnapshot;
    
    private String afterSnapshot;
    
    private String changesSummary;
    
    private String ipAddress;
    
    private String userAgent;
}

