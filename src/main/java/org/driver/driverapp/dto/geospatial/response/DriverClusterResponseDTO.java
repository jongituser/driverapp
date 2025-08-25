package org.driver.driverapp.dto.geospatial.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverClusterResponseDTO {
    private String clusterId;
    private Double centerLat;
    private Double centerLong;
    private Integer driverCount;
    private List<DriverLocationResponseDTO> drivers;
    private String region;
    private String woreda;
    private Double radiusKm;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClusterSummaryDTO {
        private Integer totalClusters;
        private Integer totalDrivers;
        private List<DriverClusterResponseDTO> clusters;
    }
}
