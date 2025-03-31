package com.ems.auth_service.dto.user;

import com.ems.auth_service.utils.customValidators.strongPasswordValidator.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAdminDTO {
    @NotBlank(message = "firstName is required")
    String firstName;

    @NotBlank(message = "lastName is required")
    String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    public String email;

    @NotBlank(message = "Password is required")
    @StrongPassword()
    public String password;
}
