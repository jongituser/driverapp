package org.driver.driverapp.service.geospatial;

import org.driver.driverapp.dto.geospatial.request.GetRouteRequestDTO;
import org.driver.driverapp.dto.geospatial.response.RouteResponseDTO;

public interface RouteService {
    
    /**
     * Calculate route between pickup and dropoff addresses
     */
    RouteResponseDTO calculateRoute(GetRouteRequestDTO request);
    
    /**
     * Calculate ETA based on distance and average speed
     */
    java.time.Duration calculateETA(Double distanceKm, String transportMode);
    
    /**
     * Optimize route for multiple waypoints
     */
    RouteResponseDTO optimizeRoute(GetRouteRequestDTO request);
    
    /**
     * Get route with traffic information
     */
    RouteResponseDTO getRouteWithTraffic(GetRouteRequestDTO request);
}
