package org.driver.driverapp.dto.analytics.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.AnalyticsRecordType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnalyticsRecordRequestDTO {
    
    @NotNull(message = "Type is required")
    private AnalyticsRecordType type;
    
    @NotNull(message = "Entity ID is required")
    private Long entityId;
    
    @NotNull(message = "Data is required")
    private String data;
}

