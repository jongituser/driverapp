package org.driver.driverapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.geospatial.request.*;
import org.driver.driverapp.dto.geospatial.response.*;
import org.driver.driverapp.model.GeoPoint;
import org.driver.driverapp.service.GeospatialService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/geospatial")
@RequiredArgsConstructor
public class GeospatialController {
    
    private final GeospatialService geospatialService;
    
    @PostMapping("/track-location")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverLocationResponseDTO> trackDriverLocation(
            @Valid @RequestBody TrackDriverLocationRequestDTO request) {
        log.info("Received location update for driver {} on delivery {}", 
                request.getDriverId(), request.getDeliveryId());
        
        DriverLocationResponseDTO response = geospatialService.trackDriverLocation(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/route")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'ADMIN')")
    public ResponseEntity<RouteResponseDTO> getRoute(@Valid @RequestBody GetRouteRequestDTO request) {
        log.info("Route calculation requested from pickup {} to dropoff {}", 
                request.getPickupAddressId(), request.getDropoffAddressId());
        
        RouteResponseDTO response = geospatialService.getRoute(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/drivers/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<DriverLocationResponseDTO>> getActiveDriversOnMap() {
        log.info("Getting active drivers on map");
        
        List<DriverLocationResponseDTO> drivers = geospatialService.getActiveDriversOnMap();
        return ResponseEntity.ok(drivers);
    }
    
    @GetMapping("/drivers/radius")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<DriverLocationResponseDTO>> getDriversInRadius(
            @RequestParam Double centerLat,
            @RequestParam Double centerLong,
            @RequestParam Double radiusKm) {
        log.info("Getting drivers within {} km of ({}, {})", radiusKm, centerLat, centerLong);
        
        List<DriverLocationResponseDTO> drivers = geospatialService.getDriversInRadius(centerLat, centerLong, radiusKm);
        return ResponseEntity.ok(drivers);
    }
    
    @PostMapping("/geofencing/setup")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<GeofencingAlertResponseDTO> setupGeofencingAlert(
            @Valid @RequestBody GeofencingAlertRequestDTO request) {
        log.info("Setting up geofencing alert for driver {} on delivery {}", 
                request.getDriverId(), request.getDeliveryId());
        
        GeofencingAlertResponseDTO response = geospatialService.setupGeofencingAlert(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/geofencing/alerts")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<GeofencingAlertResponseDTO>> getGeofencingAlerts(
            @RequestParam Long driverId,
            @RequestParam Long deliveryId) {
        log.info("Getting geofencing alerts for driver {} on delivery {}", driverId, deliveryId);
        
        List<GeofencingAlertResponseDTO> alerts = geospatialService.getGeofencingAlerts(driverId, deliveryId);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/drivers/clustered")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DriverClusterResponseDTO.ClusterSummaryDTO> getClusteredDriversView(
            @RequestParam(defaultValue = "5.0") Double clusterRadiusKm) {
        log.info("Getting clustered drivers view with radius {} km", clusterRadiusKm);
        
        DriverClusterResponseDTO.ClusterSummaryDTO response = geospatialService.getClusteredDriversView(clusterRadiusKm);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/heatmap/deliveries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HeatmapResponseDTO.HeatmapSummaryDTO> getDeliveryHeatmap(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String woreda) {
        log.info("Getting delivery heatmap for region: {}, woreda: {}", region, woreda);
        
        HeatmapResponseDTO.HeatmapSummaryDTO response = geospatialService.getDeliveryHeatmap(region, woreda);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/drivers/{driverId}/location-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<Page<GeoPoint>> getDriverLocationHistory(
            @PathVariable Long driverId,
            @PageableDefault(size = 50) Pageable pageable) {
        log.info("Getting location history for driver {}", driverId);
        
        Page<GeoPoint> history = geospatialService.getDriverLocationHistory(driverId, pageable);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/deliveries/{deliveryId}/route-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<Page<GeoPoint>> getDeliveryRouteHistory(
            @PathVariable Long deliveryId,
            @PageableDefault(size = 50) Pageable pageable) {
        log.info("Getting route history for delivery {}", deliveryId);
        
        Page<GeoPoint> history = geospatialService.getDeliveryRouteHistory(deliveryId, pageable);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Geospatial service is healthy");
    }
}
