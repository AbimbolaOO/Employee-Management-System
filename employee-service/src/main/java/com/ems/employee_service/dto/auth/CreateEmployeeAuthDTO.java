package com.ems.employee_service.dto.auth;

import com.ems.employee_service.enums.Role;

public record CreateEmployeeAuthDTO(String email, String firstName, Role role, long employeeId) {
}
