package org.driver.driverapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDriverRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name must be 50 characters or fewer")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9\\-+]{8,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must be 100 characters or fewer")
    private String email;

    @NotBlank(message = "License number is required")
    @Size(max = 30, message = "License number must be 30 characters or fewer")
    private String licenseNumber;

    @Size(max = 30, message = "Vehicle type must be 30 characters or fewer")
    private String vehicleType;

    @Size(max = 20, message = "Vehicle plate number must be 20 characters or fewer")
    private String vehiclePlateNumber;

    @Size(max = 20, message = "Vehicle color must be 20 characters or fewer")
    private String vehicleColor;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @Size(max = 255, message = "Profile image URL must be 255 characters or fewer")
    private String profileImageUrl;
}
