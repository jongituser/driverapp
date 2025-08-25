package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.analytics.request.CreateAnalyticsRecordRequestDTO;
import org.driver.driverapp.dto.analytics.response.AnalyticsRecordResponseDTO;
import org.driver.driverapp.model.AnalyticsRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AnalyticsRecordMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AnalyticsRecord toEntity(CreateAnalyticsRecordRequestDTO dto);
    
    @Mapping(target = "active", source = "active")
    AnalyticsRecordResponseDTO toResponseDTO(AnalyticsRecord entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AnalyticsRecord updateEntityFromDto(CreateAnalyticsRecordRequestDTO dto, @MappingTarget AnalyticsRecord entity);
}
