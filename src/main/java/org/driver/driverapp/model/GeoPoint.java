package org.driver.driverapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "geo_points")
@EntityListeners(AuditingEntityListener.class)
public class GeoPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "driver_id", nullable = false)
    private Long driverId;
    
    @Column(name = "delivery_id", nullable = false)
    private Long deliveryId;
    
    @Column(name = "lat", nullable = false)
    private Double lat;
    
    @Column(name = "long", nullable = false)
    private Double longitude;
    
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
    
    @Column(name = "speed_kmh")
    private Double speedKmh;
    
    @Column(name = "heading_degrees")
    private Double headingDegrees;
    
    @Column(name = "accuracy_meters")
    private Double accuracyMeters;
    
    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;
    
    @Version
    @Column(name = "version")
    private Long version;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    public boolean isValidLocation() {
        return lat != null && longitude != null && 
               lat >= -90 && lat <= 90 && 
               longitude >= -180 && longitude <= 180;
    }
    
    public double distanceTo(GeoPoint other) {
        if (!isValidLocation() || !other.isValidLocation()) {
            return -1;
        }
        
        // Haversine formula for distance calculation
        double lat1 = Math.toRadians(this.lat);
        double lon1 = Math.toRadians(this.longitude);
        double lat2 = Math.toRadians(other.lat);
        double lon2 = Math.toRadians(other.longitude);
        
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // Earth's radius in kilometers
        double earthRadius = 6371;
        
        return earthRadius * c;
    }
    
    public boolean isWithinRadius(GeoPoint center, double radiusKm) {
        return distanceTo(center) <= radiusKm;
    }
}
