package org.driver.driverapp.dto.token;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterUserRequestDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Full name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "License number is required")
    private String licenseNumber;
}
