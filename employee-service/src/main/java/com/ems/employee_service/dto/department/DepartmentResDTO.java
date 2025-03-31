package com.ems.employee_service.dto.department;

import com.ems.employee_service.entity.Department;
import lombok.Data;

@Data
public class DepartmentResDTO {
    private Long id;
    private String name;
    private String description;


    public DepartmentResDTO(Department department) {
        this.id = department.getId();
        this.name = department.getName();
        this.description = department.getDescription();
    }
}