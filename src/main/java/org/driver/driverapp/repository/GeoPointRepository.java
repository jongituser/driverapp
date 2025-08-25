package org.driver.driverapp.repository;

import org.driver.driverapp.model.GeoPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface GeoPointRepository extends JpaRepository<GeoPoint, Long> {
    
    // Find latest location for a driver
    Optional<GeoPoint> findFirstByDriverIdAndActiveTrueOrderByTimestampDesc(Long driverId);
    
    // Find latest location for a driver on a specific delivery
    Optional<GeoPoint> findFirstByDriverIdAndDeliveryIdAndActiveTrueOrderByTimestampDesc(Long driverId, Long deliveryId);
    
    // Find all locations for a driver within time range
    List<GeoPoint> findByDriverIdAndTimestampBetweenAndActiveTrueOrderByTimestampAsc(
            Long driverId, Instant startTime, Instant endTime);
    
    // Find all locations for a delivery within time range
    List<GeoPoint> findByDeliveryIdAndTimestampBetweenAndActiveTrueOrderByTimestampAsc(
            Long deliveryId, Instant startTime, Instant endTime);
    
    // Find all active drivers' latest locations
    @Query("SELECT gp FROM GeoPoint gp WHERE gp.id IN (" +
           "SELECT MAX(gp2.id) FROM GeoPoint gp2 WHERE gp2.active = true GROUP BY gp2.driverId)")
    List<GeoPoint> findAllLatestDriverLocations();
    
    // Find drivers within a radius of a point
    @Query("SELECT gp FROM GeoPoint gp WHERE gp.id IN (" +
           "SELECT MAX(gp2.id) FROM GeoPoint gp2 WHERE gp2.active = true GROUP BY gp2.driverId) " +
           "AND (6371 * acos(cos(radians(:centerLat)) * cos(radians(gp.lat)) * " +
           "cos(radians(gp.longitude) - radians(:centerLong)) + sin(radians(:centerLat)) * " +
           "sin(radians(gp.lat)))) <= :radiusKm")
    List<GeoPoint> findDriversWithinRadius(
            @Param("centerLat") Double centerLat,
            @Param("centerLong") Double centerLong,
            @Param("radiusKm") Double radiusKm);
    
    // Find drivers in a specific region (approximate)
    @Query("SELECT gp FROM GeoPoint gp WHERE gp.id IN (" +
           "SELECT MAX(gp2.id) FROM GeoPoint gp2 WHERE gp2.active = true GROUP BY gp2.driverId) " +
           "AND gp.lat BETWEEN :minLat AND :maxLat " +
           "AND gp.longitude BETWEEN :minLong AND :maxLong")
    List<GeoPoint> findDriversInBoundingBox(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLong") Double minLong,
            @Param("maxLong") Double maxLong);
    
    // Find all locations for a driver with pagination
    Page<GeoPoint> findByDriverIdAndActiveTrueOrderByTimestampDesc(Long driverId, Pageable pageable);
    
    // Find all locations for a delivery with pagination
    Page<GeoPoint> findByDeliveryIdAndActiveTrueOrderByTimestampDesc(Long deliveryId, Pageable pageable);
    
    // Count active drivers
    @Query("SELECT COUNT(DISTINCT gp.driverId) FROM GeoPoint gp WHERE gp.active = true " +
           "AND gp.timestamp >= :since")
    Long countActiveDriversSince(@Param("since") Instant since);
    
    // Find drivers who haven't updated location recently
    @Query("SELECT gp.driverId FROM GeoPoint gp WHERE gp.id IN (" +
           "SELECT MAX(gp2.id) FROM GeoPoint gp2 WHERE gp2.active = true GROUP BY gp2.driverId) " +
           "AND gp.timestamp < :cutoffTime")
    List<Long> findInactiveDrivers(@Param("cutoffTime") Instant cutoffTime);
    
    // Find delivery route points
    List<GeoPoint> findByDeliveryIdAndActiveTrueOrderByTimestampAsc(Long deliveryId);
    
    // Find recent locations for multiple drivers
    @Query("SELECT gp FROM GeoPoint gp WHERE gp.driverId IN :driverIds AND gp.active = true " +
           "AND gp.id IN (SELECT MAX(gp2.id) FROM GeoPoint gp2 WHERE gp2.driverId IN :driverIds " +
           "AND gp2.active = true GROUP BY gp2.driverId)")
    List<GeoPoint> findLatestLocationsForDrivers(@Param("driverIds") List<Long> driverIds);
    
    // Delete old location data
    @Query("DELETE FROM GeoPoint gp WHERE gp.timestamp < :cutoffTime")
    void deleteOldLocations(@Param("cutoffTime") Instant cutoffTime);
}
