package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.notification.response.NotificationResponseDTO;
import org.driver.driverapp.model.Notification;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponseDTO toResponseDTO(Notification notification);

    List<NotificationResponseDTO> toResponseDTOList(List<Notification> notifications);
}
