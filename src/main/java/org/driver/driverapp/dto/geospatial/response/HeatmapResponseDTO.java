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
public class HeatmapResponseDTO {
    private String region;
    private String woreda;
    private Integer deliveryCount;
    private Double centerLat;
    private Double centerLong;
    private Double intensity; // 0.0 to 1.0
    private String color; // hex color based on intensity
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeatmapSummaryDTO {
        private Integer totalDeliveries;
        private Integer totalRegions;
        private Integer totalWoredas;
        private List<HeatmapResponseDTO> heatmapData;
    }
}
