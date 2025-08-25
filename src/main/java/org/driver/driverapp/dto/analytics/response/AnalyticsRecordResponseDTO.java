package org.driver.driverapp.dto.analytics.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.AnalyticsRecordType;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsRecordResponseDTO {
    
    private Long id;
    private AnalyticsRecordType type;
    private Long entityId;
    private String data;
    private boolean active;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;
}

