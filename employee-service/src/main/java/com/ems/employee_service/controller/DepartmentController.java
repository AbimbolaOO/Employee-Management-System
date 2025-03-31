package com.ems.employee_service.controller;

import com.ems.employee_service.dto.department.CreateDepartmentDTO;
import com.ems.employee_service.dto.department.DepartmentResDTO;
import com.ems.employee_service.dto.department.UpdateDepartmentDTO;
import com.ems.employee_service.entity.Department;
import com.ems.employee_service.service.DepartmentService;
import com.ems.employee_service.utils.pagination.PaginatedResponseDTO;
import com.ems.employee_service.utils.pagination.PaginationQueryDTO;
import com.ems.employee_service.utils.responseBody.ResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/employee-service/departments")
@Tag(name = "Department", description = "APIs for managing department data (NOTE: you need departments to add employees)")
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new department",
            description = "Adds a new department to the system. Requires admin privileges.")
    public ResponseEntity<ResponseBody<Department>> createDepartment(
            @Valid @RequestBody @Parameter(description = "Department details to create", required = true)
            CreateDepartmentDTO createDepartmentDTO) {
        Department response = departmentService.createDepartment(createDepartmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseBody.success("Employee created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a department",
            description = "Updates an existing department's details. Requires admin privileges.")
    public ResponseEntity<ResponseBody<Map<String, String>>> updateDepartment(
            @Valid @RequestBody @Parameter(description = "Updated department details", required = true)
            UpdateDepartmentDTO updateDepartmentDTO,
            @Parameter(description = "ID of the department to update", required = true) @PathVariable("id") Long id) {
        Map<String, String> response = departmentService.updateDepartment(updateDepartmentDTO, id);
        return ResponseEntity.ok(ResponseBody.success("Department data", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a department", description = "Deletes a department by ID. Requires admin privileges.")
    public ResponseEntity<Void> deleteDepartment(
            @Parameter(description = "ID of the department to delete", required = true) @PathVariable("id") Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'MANAGER')")
    @Operation(summary = "Get a department by ID",
            description = "Retrieves a department's details by ID. Accessible to admins.")
    public ResponseEntity<ResponseBody<DepartmentResDTO>> getDepartmentById(
            @Parameter(description = "ID of the department to retrieve", required = true) @PathVariable("id") Long id) {
        DepartmentResDTO response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(ResponseBody.success("Department data", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE', 'MANAGER')")
    @Operation(summary = "Get all departments", description = "Retrieves a list of all departments. Requires admin privileges.")
    public ResponseEntity<ResponseBody<PaginatedResponseDTO<DepartmentResDTO>>> getAllDepartments(
            @Valid PaginationQueryDTO paginationQueryDTO) {
        PaginatedResponseDTO<DepartmentResDTO> response =
                PaginatedResponseDTO.from(departmentService.getAllDepartments(paginationQueryDTO));

        return ResponseEntity.ok(ResponseBody.success("Department data", response));
    }
}