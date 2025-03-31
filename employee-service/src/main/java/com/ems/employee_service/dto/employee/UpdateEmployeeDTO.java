package com.ems.employee_service.dto.employee;

import com.ems.employee_service.enums.Role;
import com.ems.employee_service.enums.Status;
import com.ems.employee_service.utils.customValidators.enumValidator.EnumValidator;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record UpdateEmployeeDTO(
        @Nullable
        @EnumValidator(enumClass = Role.class, message = "Invalid role. Allowed values: ADMIN, USER, MANAGER")
        Role role,

        @Nullable
        Long departmentId,

        @NotNull(message = "status is required")
        @EnumValidator(enumClass = Status.class, message = "Invalid status. Allowed values: FULL_TIME, PART_TIME")
        Status status
) {
}
