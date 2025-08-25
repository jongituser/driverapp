package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.driver.response.DriverEarningResponseDTO;
import org.driver.driverapp.model.DriverEarning;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DriverEarningMapper {

    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "driverName", source = "driver.name")
    @Mapping(target = "deliveryId", source = "delivery.id")
    @Mapping(target = "deliveryCode", source = "delivery.deliveryCode")
    DriverEarningResponseDTO toResponseDTO(DriverEarning driverEarning);

    List<DriverEarningResponseDTO> toResponseDTOList(List<DriverEarning> driverEarnings);
}
