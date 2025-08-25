package org.driver.driverapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.driver.driverapp.enums.AnalyticsRecordType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "analytics_records")
@EntityListeners(AuditingEntityListener.class)
public class AnalyticsRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AnalyticsRecordType type;
    
    @Column(name = "entity_id", nullable = false)
    private Long entityId;
    
    @Column(name = "data", nullable = false, columnDefinition = "JSONB")
    private String data;
    
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
}

