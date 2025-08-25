package org.driver.driverapp.service;

import org.driver.driverapp.dto.geospatial.request.*;
import org.driver.driverapp.dto.geospatial.response.*;
import org.driver.driverapp.enums.GeofenceStatus;
import org.driver.driverapp.mapper.GeoPointMapper;
import org.driver.driverapp.model.GeoPoint;
import org.driver.driverapp.repository.GeoPointRepository;
import org.driver.driverapp.service.geospatial.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeospatialServiceTest {
    
    @Mock
    private GeoPointRepository geoPointRepository;
    
    @Mock
    private RouteService routeService;
    
    @Mock
    private GeoPointMapper geoPointMapper;
    
    @InjectMocks
    private GeospatialService geospatialService;
    
    private GeoPoint testGeoPoint;
    private DriverLocationResponseDTO testDriverLocationResponse;
    private RouteResponseDTO testRouteResponse;
    
    @BeforeEach
    void setUp() {
        testGeoPoint = GeoPoint.builder()
                .id(1L)
                .driverId(1L)
                .deliveryId(1L)
                .lat(9.1450)
                .longitude(40.4897)
                .timestamp(Instant.now())
                .speedKmh(25.0)
                .headingDegrees(45.0)
                .accuracyMeters(10.0)
                .active(true)
                .build();
        
        testDriverLocationResponse = DriverLocationResponseDTO.builder()
                .driverId(1L)
                .deliveryId(1L)
                .lat(9.1450)
                .longitude(40.4897)
                .timestamp(Instant.now())
                .speedKmh(25.0)
                .headingDegrees(45.0)
                .accuracyMeters(10.0)
                .driverName("Driver 1")
                .vehicleInfo("Vehicle 1")
                .deliveryStatus("IN_PROGRESS")
                .isOnline(true)
                .build();
        
        testRouteResponse = RouteResponseDTO.builder()
                .routeId(1L)
                .totalDistanceKm(5.0)
                .estimatedDuration(java.time.Duration.ofMinutes(15))
                .estimatedDurationWithTraffic(java.time.Duration.ofMinutes(20))
                .transportMode("driving")
                .optimized(false)
                .build();
    }
    
    @Test
    void trackDriverLocation_Success() {
        // Arrange
        TrackDriverLocationRequestDTO request = TrackDriverLocationRequestDTO.builder()
                .driverId(1L)
                .deliveryId(1L)
                .lat(9.1450)
                .longitude(40.4897)
                .speedKmh(25.0)
                .headingDegrees(45.0)
                .accuracyMeters(10.0)
                .build();
        
        when(geoPointRepository.save(any(GeoPoint.class))).thenReturn(testGeoPoint);
        when(geoPointMapper.toDriverLocationResponseDTO(any(GeoPoint.class))).thenReturn(testDriverLocationResponse);
        
        // Act
        DriverLocationResponseDTO result = geospatialService.trackDriverLocation(request);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getDriverId());
        assertEquals(1L, result.getDeliveryId());
        assertEquals(9.1450, result.getLat());
        assertEquals(40.4897, result.getLongitude());
        assertEquals("Driver 1", result.getDriverName());
        assertEquals("Vehicle 1", result.getVehicleInfo());
        assertEquals("IN_PROGRESS", result.getDeliveryStatus());
        assertTrue(result.getIsOnline());
        
        verify(geoPointRepository).save(any(GeoPoint.class));
        verify(geoPointMapper).toDriverLocationResponseDTO(any(GeoPoint.class));
    }
    
    @Test
    void trackDriverLocation_InvalidCoordinates_ThrowsException() {
        // Arrange
        TrackDriverLocationRequestDTO request = TrackDriverLocationRequestDTO.builder()
                .driverId(1L)
                .deliveryId(1L)
                .lat(100.0) // Invalid latitude
                .longitude(40.4897)
                .build();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            geospatialService.trackDriverLocation(request);
        });
        
        verify(geoPointRepository, never()).save(any(GeoPoint.class));
    }
    
    @Test
    void getRoute_Success() {
        // Arrange
        GetRouteRequestDTO request = GetRouteRequestDTO.builder()
                .pickupAddressId(1L)
                .dropoffAddressId(2L)
                .transportMode("driving")
                .optimizeRoute(false)
                .includeTraffic(false)
                .build();
        
        when(routeService.calculateRoute(request)).thenReturn(testRouteResponse);
        
        // Act
        RouteResponseDTO result = geospatialService.getRoute(request);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getRouteId());
        assertEquals(5.0, result.getTotalDistanceKm());
        assertEquals("driving", result.getTransportMode());
        assertFalse(result.isOptimized());
        
        verify(routeService).calculateRoute(request);
    }
    
    @Test
    void getRoute_WithOptimization_Success() {
        // Arrange
        GetRouteRequestDTO request = GetRouteRequestDTO.builder()
                .pickupAddressId(1L)
                .dropoffAddressId(2L)
                .transportMode("driving")
                .optimizeRoute(true)
                .includeTraffic(false)
                .build();
        
        RouteResponseDTO optimizedRoute = RouteResponseDTO.builder()
                .routeId(testRouteResponse.getRouteId())
                .totalDistanceKm(testRouteResponse.getTotalDistanceKm())
                .estimatedDuration(testRouteResponse.getEstimatedDuration())
                .estimatedDurationWithTraffic(testRouteResponse.getEstimatedDurationWithTraffic())
                .transportMode(testRouteResponse.getTransportMode())
                .optimized(true)
                .waypoints(testRouteResponse.getWaypoints())
                .bounds(testRouteResponse.getBounds())
                .polyline(testRouteResponse.getPolyline())
                .warnings("Route optimized for efficiency")
                .build();
        
        when(routeService.optimizeRoute(request)).thenReturn(optimizedRoute);
        
        // Act
        RouteResponseDTO result = geospatialService.getRoute(request);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isOptimized());
        assertEquals("Route optimized for efficiency", result.getWarnings());
        
        verify(routeService).optimizeRoute(request);
        verify(routeService, never()).calculateRoute(any());
    }
    
    @Test
    void getActiveDriversOnMap_Success() {
        // Arrange
        List<GeoPoint> geoPoints = Arrays.asList(testGeoPoint);
        
        when(geoPointRepository.findAllLatestDriverLocations()).thenReturn(geoPoints);
        when(geoPointMapper.toDriverLocationResponseDTO(testGeoPoint)).thenReturn(testDriverLocationResponse);
        
        // Act
        List<DriverLocationResponseDTO> result = geospatialService.getActiveDriversOnMap();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getDriverId());
        assertEquals("ACTIVE", result.get(0).getDeliveryStatus());
        
        verify(geoPointRepository).findAllLatestDriverLocations();
        verify(geoPointMapper).toDriverLocationResponseDTO(testGeoPoint);
    }
    
    @Test
    void getDriversInRadius_Success() {
        // Arrange
        Double centerLat = 9.1500;
        Double centerLong = 40.5000;
        Double radiusKm = 5.0;
        
        List<GeoPoint> geoPoints = Arrays.asList(testGeoPoint);
        
        when(geoPointRepository.findDriversWithinRadius(centerLat, centerLong, radiusKm)).thenReturn(geoPoints);
        when(geoPointMapper.toDriverLocationResponseDTO(testGeoPoint)).thenReturn(testDriverLocationResponse);
        
        // Act
        List<DriverLocationResponseDTO> result = geospatialService.getDriversInRadius(centerLat, centerLong, radiusKm);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getDriverId());
        
        verify(geoPointRepository).findDriversWithinRadius(centerLat, centerLong, radiusKm);
        verify(geoPointMapper).toDriverLocationResponseDTO(testGeoPoint);
    }
    
    @Test
    void setupGeofencingAlert_Success() {
        // Arrange
        GeofencingAlertRequestDTO request = GeofencingAlertRequestDTO.builder()
                .driverId(1L)
                .deliveryId(1L)
                .centerLat(9.1500)
                .centerLong(40.5000)
                .radiusKm(2.0)
                .enableAlerts(true)
                .build();
        
        // Act
        GeofencingAlertResponseDTO result = geospatialService.setupGeofencingAlert(request);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getDriverId());
        assertEquals(1L, result.getDeliveryId());
        assertEquals(GeofenceStatus.INSIDE_ZONE, result.getStatus());
        assertEquals(9.1500, result.getZoneCenterLat());
        assertEquals(40.5000, result.getZoneCenterLong());
        assertEquals(2.0, result.getZoneRadiusKm());
        assertTrue(result.getIsActive());
        assertEquals("Geofencing alert activated", result.getAlertMessage());
    }
    
    @Test
    void getGeofencingAlerts_Success() {
        // Arrange
        Long driverId = 1L;
        Long deliveryId = 1L;
        
        GeofencingAlertRequestDTO geofence = GeofencingAlertRequestDTO.builder()
                .driverId(driverId)
                .deliveryId(deliveryId)
                .centerLat(9.1500)
                .centerLong(40.5000)
                .radiusKm(2.0)
                .enableAlerts(true)
                .build();
        
        // Setup geofence first
        geospatialService.setupGeofencingAlert(geofence);
        
        when(geoPointRepository.findFirstByDriverIdAndDeliveryIdAndActiveTrueOrderByTimestampDesc(driverId, deliveryId))
                .thenReturn(Optional.of(testGeoPoint));
        
        // Act
        List<GeofencingAlertResponseDTO> result = geospatialService.getGeofencingAlerts(driverId, deliveryId);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(driverId, result.get(0).getDriverId());
        assertEquals(deliveryId, result.get(0).getDeliveryId());
        assertNotNull(result.get(0).getStatus());
        assertTrue(result.get(0).getIsActive());
        
        verify(geoPointRepository).findFirstByDriverIdAndDeliveryIdAndActiveTrueOrderByTimestampDesc(driverId, deliveryId);
    }
    
    @Test
    void getClusteredDriversView_Success() {
        // Arrange
        Double clusterRadiusKm = 5.0;
        
        GeoPoint geoPoint1 = GeoPoint.builder().driverId(1L).lat(9.1450).longitude(40.4897).timestamp(Instant.now()).build();
        GeoPoint geoPoint2 = GeoPoint.builder().driverId(2L).lat(9.1460).longitude(40.4900).timestamp(Instant.now()).build();
        List<GeoPoint> geoPoints = Arrays.asList(geoPoint1, geoPoint2);
        
        DriverLocationResponseDTO response1 = DriverLocationResponseDTO.builder().driverId(1L).build();
        DriverLocationResponseDTO response2 = DriverLocationResponseDTO.builder().driverId(2L).build();
        
        when(geoPointRepository.findAllLatestDriverLocations()).thenReturn(geoPoints);
        when(geoPointMapper.toDriverLocationResponseDTO(geoPoint1)).thenReturn(response1);
        when(geoPointMapper.toDriverLocationResponseDTO(geoPoint2)).thenReturn(response2);
        
        // Act
        DriverClusterResponseDTO.ClusterSummaryDTO result = geospatialService.getClusteredDriversView(clusterRadiusKm);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalDrivers());
        assertNotNull(result.getClusters());
        
        verify(geoPointRepository).findAllLatestDriverLocations();
        verify(geoPointMapper, times(2)).toDriverLocationResponseDTO(any(GeoPoint.class));
    }
    
    @Test
    void getDeliveryHeatmap_Success() {
        // Arrange
        String region = "Addis Ababa";
        String woreda = "Bole";
        
        // Act
        HeatmapResponseDTO.HeatmapSummaryDTO result = geospatialService.getDeliveryHeatmap(region, woreda);
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getHeatmapData());
        assertTrue(result.getTotalDeliveries() > 0);
        assertTrue(result.getTotalRegions() > 0);
        assertTrue(result.getTotalWoredas() > 0);
        
        // Verify heatmap data structure
        for (HeatmapResponseDTO heatmapItem : result.getHeatmapData()) {
            assertNotNull(heatmapItem.getRegion());
            assertNotNull(heatmapItem.getWoreda());
            assertNotNull(heatmapItem.getDeliveryCount());
            assertTrue(heatmapItem.getDeliveryCount() >= 0, "Delivery count should be non-negative, but was: " + heatmapItem.getDeliveryCount());
            assertNotNull(heatmapItem.getColor());
            assertTrue(heatmapItem.getIntensity() >= 0.0 && heatmapItem.getIntensity() <= 1.0, "Intensity should be between 0.0 and 1.0, but was: " + heatmapItem.getIntensity());
        }
    }
    
    @Test
    void getDriverLocationHistory_Success() {
        // Arrange
        Long driverId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<GeoPoint> geoPoints = Arrays.asList(testGeoPoint);
        Page<GeoPoint> page = new PageImpl<>(geoPoints, pageable, 1);
        
        when(geoPointRepository.findByDriverIdAndActiveTrueOrderByTimestampDesc(driverId, pageable)).thenReturn(page);
        
        // Act
        Page<GeoPoint> result = geospatialService.getDriverLocationHistory(driverId, pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(testGeoPoint, result.getContent().get(0));
        
        verify(geoPointRepository).findByDriverIdAndActiveTrueOrderByTimestampDesc(driverId, pageable);
    }
    
    @Test
    void getDeliveryRouteHistory_Success() {
        // Arrange
        Long deliveryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<GeoPoint> geoPoints = Arrays.asList(testGeoPoint);
        Page<GeoPoint> page = new PageImpl<>(geoPoints, pageable, 1);
        
        when(geoPointRepository.findByDeliveryIdAndActiveTrueOrderByTimestampDesc(deliveryId, pageable)).thenReturn(page);
        
        // Act
        Page<GeoPoint> result = geospatialService.getDeliveryRouteHistory(deliveryId, pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(testGeoPoint, result.getContent().get(0));
        
        verify(geoPointRepository).findByDeliveryIdAndActiveTrueOrderByTimestampDesc(deliveryId, pageable);
    }
    
    @Test
    void coordinateValidation_ValidCoordinates_ReturnsTrue() {
        // Test valid coordinates through public method
        TrackDriverLocationRequestDTO validRequest = TrackDriverLocationRequestDTO.builder()
                .driverId(1L)
                .deliveryId(1L)
                .lat(9.1450)
                .longitude(40.4897)
                .build();
        
        when(geoPointRepository.save(any(GeoPoint.class))).thenReturn(testGeoPoint);
        when(geoPointMapper.toDriverLocationResponseDTO(any(GeoPoint.class))).thenReturn(testDriverLocationResponse);
        
        // Should not throw exception
        assertDoesNotThrow(() -> geospatialService.trackDriverLocation(validRequest));
    }
    
    @Test
    void coordinateValidation_InvalidCoordinates_ThrowsException() {
        // Test invalid coordinates through public method
        TrackDriverLocationRequestDTO invalidRequest = TrackDriverLocationRequestDTO.builder()
                .driverId(1L)
                .deliveryId(1L)
                .lat(100.0) // Invalid latitude
                .longitude(40.4897)
                .build();
        
        // Should throw exception
        assertThrows(IllegalArgumentException.class, () -> {
            geospatialService.trackDriverLocation(invalidRequest);
        });
    }
}
