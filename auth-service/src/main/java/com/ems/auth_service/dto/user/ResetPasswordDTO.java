package com.ems.auth_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ResetPasswordDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    public String email;
}
