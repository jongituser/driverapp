package org.driver.driverapp.dto.geospatial.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponseDTO {
    private Long routeId;
    private Double totalDistanceKm;
    private Duration estimatedDuration;
    private Duration estimatedDurationWithTraffic;
    private String transportMode;
    private boolean optimized;
    private List<RouteWaypointDTO> waypoints;
    private RouteBoundsDTO bounds;
    private String polyline;
    private String warnings;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteWaypointDTO {
        private Double lat;
        private Double longitude;
        private String address;
        private Duration durationFromStart;
        private Double distanceFromStartKm;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RouteBoundsDTO {
        private Double northEastLat;
        private Double northEastLong;
        private Double southWestLat;
        private Double southWestLong;
    }
}
