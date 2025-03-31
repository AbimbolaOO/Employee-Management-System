package com.ems.employee_service.service;

import com.ems.employee_service.dto.department.CreateDepartmentDTO;
import com.ems.employee_service.dto.department.DepartmentResDTO;
import com.ems.employee_service.dto.department.UpdateDepartmentDTO;
import com.ems.employee_service.entity.Department;
import com.ems.employee_service.utils.pagination.PaginationQueryDTO;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface DepartmentService {

    Department createDepartment(CreateDepartmentDTO createDepartmentDTO);

    Map<String, String> updateDepartment(UpdateDepartmentDTO updateDepartmentDTO, Long id);

    void deleteDepartment(Long id);

    DepartmentResDTO getDepartmentById(Long id);

    Department findById(Long id);

    Page<DepartmentResDTO> getAllDepartments(PaginationQueryDTO paginationQueryDTO);
}
