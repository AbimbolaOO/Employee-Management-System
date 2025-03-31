package com.ems.employee_service.dto.department;

import jakarta.annotation.Nullable;


public record UpdateDepartmentDTO(
        @Nullable
        String name,

        @Nullable
        String description
) {
}
