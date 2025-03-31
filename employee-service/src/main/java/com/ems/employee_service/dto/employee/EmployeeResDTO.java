package com.ems.employee_service.dto.employee;

import com.ems.employee_service.entity.Employee;
import com.ems.employee_service.enums.Status;
import lombok.Data;

@Data
public class EmployeeResDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Long departmentId;
    private Status status;

    public EmployeeResDTO(Employee employee) {
        this.id = employee.getId();
        this.email = employee.getEmail();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.status = employee.getStatus();
        this.departmentId = (employee.getDepartment() != null) ? employee.getDepartment().getId() : null;
    }
}
