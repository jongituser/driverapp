package org.driver.driverapp.dto.address.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressValidationResponseDTO {

    private boolean isValid;
    private String validationMessage;
    private List<String> errors;
    private List<String> warnings;
    private AddressResponseDTO suggestedAddress;
    private String addressType; // "GPS_ONLY", "FULL_ETHIOPIAN", "HYBRID", "INVALID"
}
