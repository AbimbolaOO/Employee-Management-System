package com.ems.employee_service.service;

import com.ems.employee_service.dto.auth.CreateEmployeeAuthDTO;
import com.ems.employee_service.dto.department.CreateDepartmentDTO;
import com.ems.employee_service.dto.employee.CreateEmployeeDTO;
import com.ems.employee_service.dto.employee.CreateFirstAdminDTO;
import com.ems.employee_service.dto.employee.EmployeeResDTO;
import com.ems.employee_service.dto.employee.UpdateEmployeeDTO;
import com.ems.employee_service.entity.Department;
import com.ems.employee_service.entity.Employee;
import com.ems.employee_service.enums.Status;
import com.ems.employee_service.feign.AuthServiceClient;
import com.ems.employee_service.repository.EmployeeRepository;
import com.ems.employee_service.utils.exceptions.InternalServerException;
import com.ems.employee_service.utils.exceptions.ResourceNotFoundException;
import com.ems.employee_service.utils.exceptions.UniqueConstraintViolationException;
import com.ems.employee_service.utils.pagination.PaginationQueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository repo;
    private final DepartmentService departmentService;
    private final AuthServiceClient authServiceClient;

    @Override
    @Transactional
    public Map<String, String> createFirstAdminEmployee(CreateFirstAdminDTO createFirstAdminDTO) {
        Department department = departmentService.createDepartment(
                new CreateDepartmentDTO("Administration", "Administrative department"));

        Employee employee = Employee.builder()
                .email(createFirstAdminDTO.email())
                .lastName(createFirstAdminDTO.lastName())
                .firstName(createFirstAdminDTO.firstName())
                .status(Status.FULL_TIME)
                .department(department)
                .build();

        try {
            repo.save(employee);
            return Map.of("employeeId", String.valueOf(employee.getId()));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("duplicate key") && e.getMessage().contains("(email)")) {
                throw new UniqueConstraintViolationException("Employee already exist");
            }

            throw new InternalServerException("Internal server error", e);
        }
    }

    @Override
    @Transactional
    public EmployeeResDTO createEmployee(CreateEmployeeDTO createEmployeeDTO) {
        Department department = departmentService.findById(createEmployeeDTO.departmentId());

        Employee employee = Employee.builder()
                .email(createEmployeeDTO.email())
                .lastName(createEmployeeDTO.lastName())
                .firstName(createEmployeeDTO.firstName())
                .status(createEmployeeDTO.status())
                .department(department)
                .build();

        Employee savedEmployee;

        try {
            savedEmployee = repo.save(employee);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("duplicate key") && e.getMessage().contains("(email)")) {
                throw new UniqueConstraintViolationException("Employee already exist");
            }

            throw new InternalServerException("Internal server error", e);
        }

        authServiceClient.createEmployeeAuth(new CreateEmployeeAuthDTO(
                        createEmployeeDTO.email(),
                        createEmployeeDTO.firstName(),
                        createEmployeeDTO.role(),
                        savedEmployee.getId()
                )
        );

        return new EmployeeResDTO(savedEmployee);
    }

    @Override
    @Transactional
    public Map<String, String> updateEmployee(UpdateEmployeeDTO updateEmployeeDTO, Long employeeId) {
        Employee employee = this.findById(employeeId);

        Map<String, String> responseObj = new HashMap<>();

        if (updateEmployeeDTO.role() != null) {
            String role = String.valueOf(updateEmployeeDTO.role());
            authServiceClient.updateEmployeeRole(role, employeeId);
            responseObj.put("name", role);
        }

        if (updateEmployeeDTO.status() != null) {
            employee.setStatus(updateEmployeeDTO.status());
            responseObj.put("status", String.valueOf(updateEmployeeDTO.status()));
        }

        if (updateEmployeeDTO.departmentId() != null) {
            Department department = departmentService.findById(updateEmployeeDTO.departmentId());
            employee.setDepartment(department);
            responseObj.put("departmentId", String.valueOf(updateEmployeeDTO.departmentId()));
        }

        repo.save(employee);
        return responseObj;
    }

    @Override
    @Transactional
    public void deleteEmployee(Long employeeId) {
        Employee employee = this.findById(employeeId);
        authServiceClient.deleteAuthAccount(employeeId);
        repo.delete(employee);
    }

    @Override
    public EmployeeResDTO getEmployeeById(Long employeeId) {
        return repo.findById(employeeId).map(EmployeeResDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }

    @Override
    public Employee findById(Long employeeId) {
        return repo.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    @Override
    public Page<EmployeeResDTO> getAllEmployees(PaginationQueryDTO paginationQueryDTO) {
        int page = paginationQueryDTO.getPage() - 1;
        int size = paginationQueryDTO.getPerPage();
        Pageable pageable = PageRequest.of(page, size);

        return repo.findAll(pageable).map(EmployeeResDTO::new);
    }

    @Override
    public Page<EmployeeResDTO> getEmployeesByDepartment(Long departmentId, PaginationQueryDTO paginationQueryDTO) {
        int page = paginationQueryDTO.getPage() - 1;
        int size = paginationQueryDTO.getPerPage();
        Pageable pageable = PageRequest.of(page, size);

        return repo.findByDepartmentId(departmentId, pageable).map(EmployeeResDTO::new);
    }
}