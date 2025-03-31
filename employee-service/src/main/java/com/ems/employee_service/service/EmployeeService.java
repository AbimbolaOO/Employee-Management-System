package com.ems.employee_service.service;

import com.ems.employee_service.dto.employee.CreateEmployeeDTO;
import com.ems.employee_service.dto.employee.CreateFirstAdminDTO;
import com.ems.employee_service.dto.employee.EmployeeResDTO;
import com.ems.employee_service.dto.employee.UpdateEmployeeDTO;
import com.ems.employee_service.entity.Employee;
import com.ems.employee_service.utils.pagination.PaginationQueryDTO;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface EmployeeService {
    Map<String, String> createFirstAdminEmployee(CreateFirstAdminDTO createFirstAdminDTO);

    EmployeeResDTO createEmployee(CreateEmployeeDTO createEmployeeDTO);

    Map<String, String> updateEmployee(UpdateEmployeeDTO updateEmployeeDTO, Long id);

    void deleteEmployee(Long id);

    EmployeeResDTO getEmployeeById(Long id);

    Page<EmployeeResDTO> getAllEmployees(PaginationQueryDTO paginationQueryDTO);

    Employee findById(Long employeeId);

    Page<EmployeeResDTO> getEmployeesByDepartment(Long departmentId, PaginationQueryDTO paginationQueryDTO);
}
