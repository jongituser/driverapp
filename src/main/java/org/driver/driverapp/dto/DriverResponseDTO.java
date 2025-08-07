package org.driver.driverapp.dto;

import lombok.Builder;
import lombok.Data;
import org.driver.driverapp.enums.DriverStatus;

@Data
@Builder
public class DriverResponseDTO {

    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    private String licenseNumber;

    private String vehicleType;
    private String vehiclePlateNumber;
    private String vehicleColor;

    private String profileImageUrl;

    private DriverStatus status;
    private boolean isOnline;

    private int totalDeliveries;
    private int activeDeliveries;
}
