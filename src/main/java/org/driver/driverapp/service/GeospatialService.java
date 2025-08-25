package org.driver.driverapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.driver.driverapp.dto.geospatial.request.*;
import org.driver.driverapp.dto.geospatial.response.*;
import org.driver.driverapp.enums.GeofenceStatus;
import org.driver.driverapp.mapper.GeoPointMapper;
import org.driver.driverapp.model.GeoPoint;
import org.driver.driverapp.repository.GeoPointRepository;
import org.driver.driverapp.service.geospatial.RouteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeospatialService {
    
    private final GeoPointRepository geoPointRepository;
    private final RouteService routeService;
    private final GeoPointMapper geoPointMapper;
    
    // Geofencing cache to store active geofences
    private final Map<String, GeofencingAlertRequestDTO> activeGeofences = new HashMap<>();
    
    @Transactional
    public DriverLocationResponseDTO trackDriverLocation(TrackDriverLocationRequestDTO request) {
        log.info("Tracking driver {} location for delivery {}", request.getDriverId(), request.getDeliveryId());
        
        // Validate coordinates
        if (!isValidCoordinates(request.getLat(), request.getLongitude())) {
            throw new IllegalArgumentException("Invalid GPS coordinates");
        }
        
        // Create and save geo point
        GeoPoint geoPoint = GeoPoint.builder()
                .driverId(request.getDriverId())
                .deliveryId(request.getDeliveryId())
                .lat(request.getLat())
                .longitude(request.getLongitude())
                .timestamp(Instant.now())
                .speedKmh(request.getSpeedKmh())
                .headingDegrees(request.getHeadingDegrees())
                .accuracyMeters(request.getAccuracyMeters())
                .build();
        
        GeoPoint savedGeoPoint = geoPointRepository.save(geoPoint);
        
        // Check geofencing alerts
        checkGeofencingAlerts(request.getDriverId(), request.getDeliveryId(), savedGeoPoint);
        
        // Map to response DTO
        DriverLocationResponseDTO response = geoPointMapper.toDriverLocationResponseDTO(savedGeoPoint);
        
        // Add additional driver information (in real implementation, fetch from driver service)
        response.setDriverName("Driver " + request.getDriverId());
        response.setVehicleInfo("Vehicle " + request.getDriverId());
        response.setDeliveryStatus("IN_PROGRESS");
        response.setIsOnline(true);
        
        return response;
    }
    
    public RouteResponseDTO getRoute(GetRouteRequestDTO request) {
        log.info("Getting route from pickup {} to dropoff {}", 
                request.getPickupAddressId(), request.getDropoffAddressId());
        
        if (request.isOptimizeRoute()) {
            return routeService.optimizeRoute(request);
        } else if (request.isIncludeTraffic()) {
            return routeService.getRouteWithTraffic(request);
        } else {
            return routeService.calculateRoute(request);
        }
    }
    
    public List<DriverLocationResponseDTO> getActiveDriversOnMap() {
        log.info("Getting all active drivers on map");
        
        List<GeoPoint> latestLocations = geoPointRepository.findAllLatestDriverLocations();
        
        return latestLocations.stream()
                .map(geoPoint -> {
                    DriverLocationResponseDTO response = geoPointMapper.toDriverLocationResponseDTO(geoPoint);
                    response.setDriverName("Driver " + geoPoint.getDriverId());
                    response.setVehicleInfo("Vehicle " + geoPoint.getDriverId());
                    response.setDeliveryStatus("ACTIVE");
                    response.setIsOnline(isDriverOnline(geoPoint.getTimestamp()));
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    public List<DriverLocationResponseDTO> getDriversInRadius(Double centerLat, Double centerLong, Double radiusKm) {
        log.info("Getting drivers within {} km of ({}, {})", radiusKm, centerLat, centerLong);
        
        List<GeoPoint> driversInRadius = geoPointRepository.findDriversWithinRadius(centerLat, centerLong, radiusKm);
        
        return driversInRadius.stream()
                .map(geoPoint -> {
                    DriverLocationResponseDTO response = geoPointMapper.toDriverLocationResponseDTO(geoPoint);
                    response.setDriverName("Driver " + geoPoint.getDriverId());
                    response.setVehicleInfo("Vehicle " + geoPoint.getDriverId());
                    response.setDeliveryStatus("ACTIVE");
                    response.setIsOnline(isDriverOnline(geoPoint.getTimestamp()));
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    public GeofencingAlertResponseDTO setupGeofencingAlert(GeofencingAlertRequestDTO request) {
        log.info("Setting up geofencing alert for driver {} on delivery {}", 
                request.getDriverId(), request.getDeliveryId());
        
        String geofenceKey = generateGeofenceKey(request.getDriverId(), request.getDeliveryId());
        activeGeofences.put(geofenceKey, request);
        
        return GeofencingAlertResponseDTO.builder()
                .alertId(System.currentTimeMillis())
                .driverId(request.getDriverId())
                .deliveryId(request.getDeliveryId())
                .status(GeofenceStatus.INSIDE_ZONE)
                .zoneCenterLat(request.getCenterLat())
                .zoneCenterLong(request.getCenterLong())
                .zoneRadiusKm(request.getRadiusKm())
                .timestamp(Instant.now())
                .alertMessage("Geofencing alert activated")
                .isActive(true)
                .build();
    }
    
    public List<GeofencingAlertResponseDTO> getGeofencingAlerts(Long driverId, Long deliveryId) {
        log.info("Getting geofencing alerts for driver {} on delivery {}", driverId, deliveryId);
        
        // In real implementation, this would query a geofencing alerts table
        // For now, return simulated alerts
        List<GeofencingAlertResponseDTO> alerts = new ArrayList<>();
        
        String geofenceKey = generateGeofenceKey(driverId, deliveryId);
        GeofencingAlertRequestDTO geofence = activeGeofences.get(geofenceKey);
        
        if (geofence != null) {
            // Get latest driver location
            Optional<GeoPoint> latestLocation = geoPointRepository
                    .findFirstByDriverIdAndDeliveryIdAndActiveTrueOrderByTimestampDesc(driverId, deliveryId);
            
            if (latestLocation.isPresent()) {
                GeoPoint location = latestLocation.get();
                GeofenceStatus status = determineGeofenceStatus(location, geofence);
                
                alerts.add(GeofencingAlertResponseDTO.builder()
                        .alertId(System.currentTimeMillis())
                        .driverId(driverId)
                        .deliveryId(deliveryId)
                        .status(status)
                        .driverLat(location.getLat())
                        .driverLong(location.getLongitude())
                        .zoneCenterLat(geofence.getCenterLat())
                        .zoneCenterLong(geofence.getCenterLong())
                        .zoneRadiusKm(geofence.getRadiusKm())
                        .distanceFromZoneKm(location.distanceTo(createGeoPoint(geofence.getCenterLat(), geofence.getCenterLong())))
                        .timestamp(location.getTimestamp())
                        .alertMessage(generateAlertMessage(status))
                        .isActive(true)
                        .build());
            }
        }
        
        return alerts;
    }
    
    public DriverClusterResponseDTO.ClusterSummaryDTO getClusteredDriversView(Double clusterRadiusKm) {
        log.info("Getting clustered drivers view with radius {} km", clusterRadiusKm);
        
        List<GeoPoint> allDrivers = geoPointRepository.findAllLatestDriverLocations();
        List<DriverClusterResponseDTO> clusters = clusterDrivers(allDrivers, clusterRadiusKm);
        
        return DriverClusterResponseDTO.ClusterSummaryDTO.builder()
                .totalClusters(clusters.size())
                .totalDrivers(allDrivers.size())
                .clusters(clusters)
                .build();
    }
    
    public HeatmapResponseDTO.HeatmapSummaryDTO getDeliveryHeatmap(String region, String woreda) {
        log.info("Getting delivery heatmap for region: {}, woreda: {}", region, woreda);
        
        // In real implementation, this would query delivery data and aggregate by region/woreda
        // For now, return simulated heatmap data
        List<HeatmapResponseDTO> heatmapData = generateSimulatedHeatmapData(region, woreda);
        
        return HeatmapResponseDTO.HeatmapSummaryDTO.builder()
                .totalDeliveries(heatmapData.stream().mapToInt(HeatmapResponseDTO::getDeliveryCount).sum())
                .totalRegions((int) heatmapData.stream().map(HeatmapResponseDTO::getRegion).distinct().count())
                .totalWoredas((int) heatmapData.stream().map(HeatmapResponseDTO::getWoreda).distinct().count())
                .heatmapData(heatmapData)
                .build();
    }
    
    public Page<GeoPoint> getDriverLocationHistory(Long driverId, Pageable pageable) {
        return geoPointRepository.findByDriverIdAndActiveTrueOrderByTimestampDesc(driverId, pageable);
    }
    
    public Page<GeoPoint> getDeliveryRouteHistory(Long deliveryId, Pageable pageable) {
        return geoPointRepository.findByDeliveryIdAndActiveTrueOrderByTimestampDesc(deliveryId, pageable);
    }
    
    // Helper methods
    
    private boolean isValidCoordinates(Double lat, Double longitude) {
        return lat != null && longitude != null && 
               lat >= -90 && lat <= 90 && 
               longitude >= -180 && longitude <= 180;
    }
    
    private boolean isDriverOnline(Instant lastUpdate) {
        return lastUpdate.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
    }
    
    private void checkGeofencingAlerts(Long driverId, Long deliveryId, GeoPoint currentLocation) {
        String geofenceKey = generateGeofenceKey(driverId, deliveryId);
        GeofencingAlertRequestDTO geofence = activeGeofences.get(geofenceKey);
        
        if (geofence != null && geofence.isEnableAlerts()) {
            GeofenceStatus status = determineGeofenceStatus(currentLocation, geofence);
            
            if (status == GeofenceStatus.EXITING_ZONE || status == GeofenceStatus.OUTSIDE_ZONE) {
                log.warn("Driver {} has left geofence zone for delivery {}", driverId, deliveryId);
                // In real implementation, send notification or trigger alert
            }
        }
    }
    
    private GeofenceStatus determineGeofenceStatus(GeoPoint location, GeofencingAlertRequestDTO geofence) {
        GeoPoint center = createGeoPoint(geofence.getCenterLat(), geofence.getCenterLong());
        double distance = location.distanceTo(center);
        
        if (distance <= geofence.getRadiusKm()) {
            return GeofenceStatus.INSIDE_ZONE;
        } else {
            return GeofenceStatus.OUTSIDE_ZONE;
        }
    }
    
    private GeoPoint createGeoPoint(Double lat, Double longitude) {
        return GeoPoint.builder()
                .lat(lat)
                .longitude(longitude)
                .timestamp(Instant.now())
                .build();
    }
    
    private String generateGeofenceKey(Long driverId, Long deliveryId) {
        return driverId + "_" + deliveryId;
    }
    
    private String generateAlertMessage(GeofenceStatus status) {
        return switch (status) {
            case INSIDE_ZONE -> "Driver is within delivery zone";
            case OUTSIDE_ZONE -> "Driver has left delivery zone";
            case ENTERING_ZONE -> "Driver is entering delivery zone";
            case EXITING_ZONE -> "Driver is exiting delivery zone";
        };
    }
    
    private List<DriverClusterResponseDTO> clusterDrivers(List<GeoPoint> drivers, Double clusterRadiusKm) {
        List<DriverClusterResponseDTO> clusters = new ArrayList<>();
        Set<Long> processedDrivers = new HashSet<>();
        
        for (GeoPoint driver : drivers) {
            if (processedDrivers.contains(driver.getDriverId())) {
                continue;
            }
            
            // Find nearby drivers
            List<GeoPoint> nearbyDrivers = drivers.stream()
                    .filter(d -> !processedDrivers.contains(d.getDriverId()))
                    .filter(d -> driver.distanceTo(d) <= clusterRadiusKm)
                    .collect(Collectors.toList());
            
            if (!nearbyDrivers.isEmpty()) {
                // Calculate cluster center
                double avgLat = nearbyDrivers.stream().mapToDouble(GeoPoint::getLat).average().orElse(0.0);
                double avgLong = nearbyDrivers.stream().mapToDouble(GeoPoint::getLongitude).average().orElse(0.0);
                
                // Convert to response DTOs
                List<DriverLocationResponseDTO> driverResponses = nearbyDrivers.stream()
                        .map(geoPoint -> {
                            DriverLocationResponseDTO response = geoPointMapper.toDriverLocationResponseDTO(geoPoint);
                            response.setDriverName("Driver " + geoPoint.getDriverId());
                            response.setVehicleInfo("Vehicle " + geoPoint.getDriverId());
                            response.setDeliveryStatus("ACTIVE");
                            response.setIsOnline(isDriverOnline(geoPoint.getTimestamp()));
                            return response;
                        })
                        .collect(Collectors.toList());
                
                clusters.add(DriverClusterResponseDTO.builder()
                        .clusterId("cluster_" + System.currentTimeMillis())
                        .centerLat(avgLat)
                        .centerLong(avgLong)
                        .driverCount(nearbyDrivers.size())
                        .drivers(driverResponses)
                        .radiusKm(clusterRadiusKm)
                        .build());
                
                // Mark drivers as processed
                nearbyDrivers.forEach(d -> processedDrivers.add(d.getDriverId()));
            }
        }
        
        return clusters;
    }
    
    private List<HeatmapResponseDTO> generateSimulatedHeatmapData(String region, String woreda) {
        List<HeatmapResponseDTO> heatmapData = new ArrayList<>();
        
        // Simulate delivery data for different areas
        String[] regions = {"Addis Ababa", "Oromia", "Amhara", "Tigray"};
        String[] woredas = {"Kolfe Keranio", "Bole", "Kirkos", "Arada"};
        
        for (int i = 0; i < regions.length; i++) {
            for (int j = 0; j < woredas.length; j++) {
                int deliveryCount = (int) (Math.random() * 100) + 10;
                double intensity = Math.min(deliveryCount / 100.0, 1.0); // Cap intensity at 1.0
                
                heatmapData.add(HeatmapResponseDTO.builder()
                        .region(regions[i])
                        .woreda(woredas[j])
                        .deliveryCount(deliveryCount)
                        .centerLat(9.0 + (i * 0.1))
                        .centerLong(40.0 + (j * 0.1))
                        .intensity(intensity)
                        .color(generateHeatmapColor(intensity))
                        .build());
            }
        }
        
        return heatmapData;
    }
    
    private String generateHeatmapColor(double intensity) {
        // Generate color from blue (low intensity) to red (high intensity)
        int red = (int) (intensity * 255);
        int blue = (int) ((1 - intensity) * 255);
        return String.format("#%02x00%02x", red, blue);
    }
}
