package com.ems.employee_service.controller;

import com.ems.employee_service.dto.employee.CreateEmployeeDTO;
import com.ems.employee_service.dto.employee.CreateFirstAdminDTO;
import com.ems.employee_service.dto.employee.EmployeeResDTO;
import com.ems.employee_service.dto.employee.UpdateEmployeeDTO;
import com.ems.employee_service.service.EmployeeService;
import com.ems.employee_service.utils.pagination.PaginatedResponseDTO;
import com.ems.employee_service.utils.pagination.PaginationQueryDTO;
import com.ems.employee_service.utils.responseBody.ResponseBody;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee-service/employees")
@Tag(name = "Employee Management", description = "APIs for managing employee data")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new employee", description = "Adds a new employee to the system. Requires admin privileges.")
    public ResponseEntity<ResponseBody<EmployeeResDTO>> createEmployee(
            @Valid @RequestBody CreateEmployeeDTO createEmployeeDTO) {
        EmployeeResDTO response = employeeService.createEmployee(createEmployeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseBody.success("Employee created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an employee", description = "Updates an existing employee's details. Requires admin privileges.")
    public ResponseEntity<ResponseBody<Map<String, String>>> updateEmployee(
            @Valid @RequestBody UpdateEmployeeDTO updateEmployeeDTO,
            @Parameter(description = "ID of the employee to update", required = true) @PathVariable("id") Long id) {
        Map<String, String> response = employeeService.updateEmployee(updateEmployeeDTO, id);
        return ResponseEntity.ok(ResponseBody.success("Employee data", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an employee", description = "Deletes an employee by ID. Requires admin privileges.")
    public ResponseEntity<Void> deleteEmployee(
            @Parameter(description = "ID of the employee to delete", required = true) @PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @Operation(summary = "Get an employee by ID",
            description = "Retrieves an employee's details by ID. Accessible to the employee, their manager, or an admin.")
    public ResponseEntity<ResponseBody<EmployeeResDTO>> getEmployeeById(
            @Parameter(description = "ID of the employee to retrieve", required = true) @PathVariable("id") Long id) {
        EmployeeResDTO response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ResponseBody.success("Employee data", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all employees", description = "Retrieves a list of all employees. Requires admin privileges.")
    public ResponseEntity<ResponseBody<PaginatedResponseDTO<EmployeeResDTO>>> getAllEmployees(
            @Valid PaginationQueryDTO paginationQueryDTO) {
        PaginatedResponseDTO<EmployeeResDTO> response =
                PaginatedResponseDTO.from(employeeService.getAllEmployees(paginationQueryDTO));
        return ResponseEntity.ok(ResponseBody.success("Employee data", response));
    }

    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employees by department",
            description = "Retrieves employees in a specific department. Requires manager privileges.")
    public ResponseEntity<ResponseBody<PaginatedResponseDTO<EmployeeResDTO>>> getEmployeesByDepartment(
            @Parameter(description = "ID of the department", required = true) @PathVariable("departmentId") Long departmentId,
            @Valid PaginationQueryDTO paginationQueryDTO
    ) {
        PaginatedResponseDTO<EmployeeResDTO> response =
                PaginatedResponseDTO.from(employeeService.getEmployeesByDepartment(departmentId, paginationQueryDTO));
        return ResponseEntity.ok(ResponseBody.success("Employee data", response));
    }

    @PostMapping("first-admin")
    @Hidden
    public ResponseEntity<Map<String, String>> createFirstAdminEmployee(@RequestBody CreateFirstAdminDTO createFirstAdminDTO) {
        return ResponseEntity.ok(this.employeeService.createFirstAdminEmployee(createFirstAdminDTO));
    }
}