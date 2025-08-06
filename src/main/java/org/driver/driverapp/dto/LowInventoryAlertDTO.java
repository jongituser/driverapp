package org.driver.driverapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LowInventoryAlertDTO {
    private String partnerName;
    private String productName;
    private int quantity;
}
