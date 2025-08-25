package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.audit.request.CreateAuditLogRequestDTO;
import org.driver.driverapp.dto.audit.response.AuditLogResponseDTO;
import org.driver.driverapp.model.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    AuditLog toEntity(CreateAuditLogRequestDTO dto);
    
    @Mapping(target = "active", source = "active")
    AuditLogResponseDTO toResponseDTO(AuditLog entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    AuditLog updateEntityFromDto(CreateAuditLogRequestDTO dto, @MappingTarget AuditLog entity);
}
