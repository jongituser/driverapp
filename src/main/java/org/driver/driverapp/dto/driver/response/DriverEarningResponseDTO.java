package org.driver.driverapp.dto.driver.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.driver.driverapp.enums.PayoutStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverEarningResponseDTO {

    private Long id;
    private Long driverId;
    private String driverName;
    private Long deliveryId;
    private String deliveryCode;
    private BigDecimal amount;
    private PayoutStatus payoutStatus;
    private String description;
    private String payoutReference;
    private Instant payoutDate;
    private String failureReason;
    private Instant createdAt;
    private Instant updatedAt;
}
