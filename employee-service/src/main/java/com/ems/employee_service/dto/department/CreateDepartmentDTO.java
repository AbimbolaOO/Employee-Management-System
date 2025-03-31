package com.ems.employee_service.dto.department;

import jakarta.validation.constraints.NotBlank;

public record CreateDepartmentDTO(
        @NotBlank(message = "name is required")
        String name,

        @NotBlank(message = "description is required")
        String description
) {
}
