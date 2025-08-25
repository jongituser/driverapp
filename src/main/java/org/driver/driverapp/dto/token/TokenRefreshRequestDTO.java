package org.driver.driverapp.dto.token;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRefreshRequestDTO {

    @NotBlank
    private String refreshToken;
}
