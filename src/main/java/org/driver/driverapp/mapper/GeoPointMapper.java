package org.driver.driverapp.mapper;

import org.driver.driverapp.dto.geospatial.response.DriverLocationResponseDTO;
import org.driver.driverapp.model.GeoPoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GeoPointMapper {
    
    @Mapping(target = "driverName", ignore = true) // Will be set by service
    @Mapping(target = "vehicleInfo", ignore = true) // Will be set by service
    @Mapping(target = "deliveryStatus", ignore = true) // Will be set by service
    @Mapping(target = "isOnline", ignore = true) // Will be set by service
    @Mapping(target = "lastUpdated", source = "timestamp")
    DriverLocationResponseDTO toDriverLocationResponseDTO(GeoPoint geoPoint);
    
    List<DriverLocationResponseDTO> toDriverLocationResponseDTOList(List<GeoPoint> geoPoints);
    
    @Named("toGeoPoint")
    default GeoPoint toGeoPoint(Double lat, Double longitude) {
        if (lat == null || longitude == null) {
            return null;
        }
        return GeoPoint.builder()
                .lat(lat)
                .longitude(longitude)
                .timestamp(java.time.Instant.now())
                .build();
    }
}
