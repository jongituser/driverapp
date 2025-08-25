package org.driver.driverapp.service.geospatial;

import org.driver.driverapp.dto.geospatial.request.GetRouteRequestDTO;
import org.driver.driverapp.dto.geospatial.response.RouteResponseDTO;
import org.driver.driverapp.service.geospatial.impl.RouteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {
    
    @InjectMocks
    private RouteServiceImpl routeService;
    
    private GetRouteRequestDTO testRequest;
    
    @BeforeEach
    void setUp() {
        testRequest = GetRouteRequestDTO.builder()
                .pickupAddressId(1L)
                .dropoffAddressId(2L)
                .transportMode("driving")
                .optimizeRoute(false)
                .includeTraffic(false)
                .build();
    }
    
    @Test
    void calculateRoute_Success() {
        // Act
        RouteResponseDTO result = routeService.calculateRoute(testRequest);
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getRouteId());
        assertTrue(result.getTotalDistanceKm() > 0);
        assertNotNull(result.getEstimatedDuration());
        assertNotNull(result.getEstimatedDurationWithTraffic());
        assertEquals("driving", result.getTransportMode());
        assertFalse(result.isOptimized());
        assertNotNull(result.getWaypoints());
        assertNotNull(result.getBounds());
        assertNotNull(result.getPolyline());
        
        // Verify waypoints structure
        assertEquals(2, result.getWaypoints().size());
        assertNotNull(result.getWaypoints().get(0).getLat());
        assertNotNull(result.getWaypoints().get(0).getLongitude());
        assertNotNull(result.getWaypoints().get(1).getLat());
        assertNotNull(result.getWaypoints().get(1).getLongitude());
    }
    
    @Test
    void calculateETA_DrivingMode_Success() {
        // Arrange
        Double distanceKm = 10.0;
        String transportMode = "driving";
        
        // Act
        Duration result = routeService.calculateETA(distanceKm, transportMode);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.getSeconds() > 0);
        
        // Should be approximately 20 minutes for 10km at 30km/h average speed
        long expectedSeconds = (long) (distanceKm / 30.0 * 3600); // 30 km/h average speed
        long tolerance = 60; // 1 minute tolerance
        assertTrue(Math.abs(result.getSeconds() - expectedSeconds) <= tolerance);
    }
    
    @Test
    void calculateETA_WalkingMode_Success() {
        // Arrange
        Double distanceKm = 5.0;
        String transportMode = "walking";
        
        // Act
        Duration result = routeService.calculateETA(distanceKm, transportMode);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.getSeconds() > 0);
        
        // Should be approximately 1 hour for 5km at 5km/h average speed
        long expectedSeconds = (long) (distanceKm / 5.0 * 3600); // 5 km/h average speed
        long tolerance = 60; // 1 minute tolerance
        assertTrue(Math.abs(result.getSeconds() - expectedSeconds) <= tolerance);
    }
    
    @Test
    void calculateETA_CyclingMode_Success() {
        // Arrange
        Double distanceKm = 15.0;
        String transportMode = "cycling";
        
        // Act
        Duration result = routeService.calculateETA(distanceKm, transportMode);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.getSeconds() > 0);
        
        // Should be approximately 1 hour for 15km at 15km/h average speed
        long expectedSeconds = (long) (distanceKm / 15.0 * 3600); // 15 km/h average speed
        long tolerance = 60; // 1 minute tolerance
        assertTrue(Math.abs(result.getSeconds() - expectedSeconds) <= tolerance);
    }
    
    @Test
    void calculateETA_ZeroDistance_ReturnsZeroDuration() {
        // Arrange
        Double distanceKm = 0.0;
        String transportMode = "driving";
        
        // Act
        Duration result = routeService.calculateETA(distanceKm, transportMode);
        
        // Assert
        assertEquals(Duration.ZERO, result);
    }
    
    @Test
    void calculateETA_NullDistance_ReturnsZeroDuration() {
        // Arrange
        Double distanceKm = null;
        String transportMode = "driving";
        
        // Act
        Duration result = routeService.calculateETA(distanceKm, transportMode);
        
        // Assert
        assertEquals(Duration.ZERO, result);
    }
    
    @Test
    void calculateETA_NegativeDistance_ReturnsZeroDuration() {
        // Arrange
        Double distanceKm = -5.0;
        String transportMode = "driving";
        
        // Act
        Duration result = routeService.calculateETA(distanceKm, transportMode);
        
        // Assert
        assertEquals(Duration.ZERO, result);
    }
    
    @Test
    void optimizeRoute_Success() {
        // Arrange
        GetRouteRequestDTO request = GetRouteRequestDTO.builder()
                .pickupAddressId(1L)
                .dropoffAddressId(2L)
                .transportMode("driving")
                .optimizeRoute(true)
                .includeTraffic(false)
                .build();
        
        // Act
        RouteResponseDTO result = routeService.optimizeRoute(request);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isOptimized());
        assertEquals("Route optimized for efficiency", result.getWarnings());
        
        // Optimized route should be marked as optimized
        assertTrue(result.isOptimized());
        assertNotNull(result.getEstimatedDuration());
        assertTrue(result.getEstimatedDuration().getSeconds() > 0);
    }
    
    @Test
    void getRouteWithTraffic_Success() {
        // Arrange
        GetRouteRequestDTO request = GetRouteRequestDTO.builder()
                .pickupAddressId(1L)
                .dropoffAddressId(2L)
                .transportMode("driving")
                .optimizeRoute(false)
                .includeTraffic(true)
                .build();
        
        // Act
        RouteResponseDTO result = routeService.getRouteWithTraffic(request);
        
        // Assert
        assertNotNull(result);
        assertNotNull(result.getEstimatedDurationWithTraffic());
        assertEquals("Traffic data is simulated - use real-time data in production", result.getWarnings());
        
        // Traffic duration should be valid
        assertNotNull(result.getEstimatedDurationWithTraffic());
        assertTrue(result.getEstimatedDurationWithTraffic().getSeconds() > 0);
    }
    
    @Test
    void routeCalculation_ConsistentResults() {
        // Test that multiple calls with same parameters return consistent results
        RouteResponseDTO result1 = routeService.calculateRoute(testRequest);
        RouteResponseDTO result2 = routeService.calculateRoute(testRequest);
        
        // Both results should be valid
        assertNotNull(result1);
        assertNotNull(result2);
        assertTrue(result1.getTotalDistanceKm() > 0);
        assertTrue(result2.getTotalDistanceKm() > 0);
        
        // Transport mode should be the same
        assertEquals(result1.getTransportMode(), result2.getTransportMode());
        assertEquals(result1.isOptimized(), result2.isOptimized());
    }
    
    @Test
    void routeCalculation_DifferentTransportModes() {
        // Test different transport modes
        GetRouteRequestDTO drivingRequest = GetRouteRequestDTO.builder()
                .pickupAddressId(testRequest.getPickupAddressId())
                .dropoffAddressId(testRequest.getDropoffAddressId())
                .transportMode("driving")
                .optimizeRoute(testRequest.isOptimizeRoute())
                .includeTraffic(testRequest.isIncludeTraffic())
                .build();
        GetRouteRequestDTO walkingRequest = GetRouteRequestDTO.builder()
                .pickupAddressId(testRequest.getPickupAddressId())
                .dropoffAddressId(testRequest.getDropoffAddressId())
                .transportMode("walking")
                .optimizeRoute(testRequest.isOptimizeRoute())
                .includeTraffic(testRequest.isIncludeTraffic())
                .build();
        GetRouteRequestDTO cyclingRequest = GetRouteRequestDTO.builder()
                .pickupAddressId(testRequest.getPickupAddressId())
                .dropoffAddressId(testRequest.getDropoffAddressId())
                .transportMode("cycling")
                .optimizeRoute(testRequest.isOptimizeRoute())
                .includeTraffic(testRequest.isIncludeTraffic())
                .build();
        
        RouteResponseDTO drivingResult = routeService.calculateRoute(drivingRequest);
        RouteResponseDTO walkingResult = routeService.calculateRoute(walkingRequest);
        RouteResponseDTO cyclingResult = routeService.calculateRoute(cyclingRequest);
        
        // All should have valid results
        assertNotNull(drivingResult);
        assertNotNull(walkingResult);
        assertNotNull(cyclingResult);
        
        // All should have valid durations
        assertTrue(drivingResult.getEstimatedDuration().getSeconds() > 0);
        assertTrue(walkingResult.getEstimatedDuration().getSeconds() > 0);
        assertTrue(cyclingResult.getEstimatedDuration().getSeconds() > 0);
    }
}
