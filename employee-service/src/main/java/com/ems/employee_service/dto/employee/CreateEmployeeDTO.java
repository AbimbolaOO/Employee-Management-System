package com.ems.employee_service.dto.employee;

import com.ems.employee_service.enums.Role;
import com.ems.employee_service.enums.Status;
import com.ems.employee_service.utils.customValidators.enumValidator.EnumValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateEmployeeDTO(
        @NotBlank(message = "firstName is required")
        String firstName,

        @NotBlank(message = "lastName is required")
        String lastName,

        @Email(message = "email must be a valid email address")
        String email,

        @NotNull(message = "role is required")
        @EnumValidator(enumClass = Role.class, message = "Invalid role. Allowed values: ADMIN, USER, MANAGER")
        Role role,

        @NotNull(message = "status is required")
        @EnumValidator(enumClass = Status.class, message = "Invalid status. Allowed values: FULL_TIME, PART_TIME")
        Status status,

        @NotNull(message = "DepartmentId is required")
        @Positive(message = "DepartmentId must be a positive number")
        Long departmentId
) {
}