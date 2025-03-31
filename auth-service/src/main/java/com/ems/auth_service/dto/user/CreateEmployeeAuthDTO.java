package com.ems.auth_service.dto.user;

import com.ems.auth_service.enums.Role;
import com.ems.auth_service.utils.customValidators.enumValidator.EnumValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateEmployeeAuthDTO {
    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    public String email;

    @NotBlank(message = "firstName is required")
    public String firstName;

    @NotNull(message = "employeeId is required")
    @Positive(message = "employeeId must be a positive number")
    public Long employeeId;

    @NotNull(message = "role is required")
    @EnumValidator(enumClass = Role.class, message = "Invalid role. Allowed values: ADMIN, USER, MANAGER")
    public Role role;
}
