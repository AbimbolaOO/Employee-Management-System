package com.ems.employee_service.dto.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateFirstAdminDTO(
        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName,

        @Email(message = "email must be a valid email address")
        String email
) {
}