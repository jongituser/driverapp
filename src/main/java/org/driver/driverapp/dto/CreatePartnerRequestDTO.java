package org.driver.driverapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePartnerRequestDTO {

    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String phone;

    @Email
    private String email;
}
