package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.notification.request.CreateNotificationTemplateRequestDTO;
import org.driver.driverapp.dto.notification.request.UpdateNotificationTemplateRequestDTO;
import org.driver.driverapp.dto.notification.response.NotificationTemplateResponseDTO;
import org.driver.driverapp.model.NotificationTemplate;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationTemplateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    NotificationTemplate toEntity(CreateNotificationTemplateRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    NotificationTemplate updateEntityFromDto(UpdateNotificationTemplateRequestDTO dto, @MappingTarget NotificationTemplate template);

    NotificationTemplateResponseDTO toResponseDTO(NotificationTemplate template);

    List<NotificationTemplateResponseDTO> toResponseDTOList(List<NotificationTemplate> templates);
}
