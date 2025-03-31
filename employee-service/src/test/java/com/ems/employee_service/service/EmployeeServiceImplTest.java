package com.ems.employee_service.service;

import com.ems.employee_service.dto.auth.CreateEmployeeAuthDTO;
import com.ems.employee_service.dto.department.CreateDepartmentDTO;
import com.ems.employee_service.dto.employee.CreateEmployeeDTO;
import com.ems.employee_service.dto.employee.CreateFirstAdminDTO;
import com.ems.employee_service.dto.employee.EmployeeResDTO;
import com.ems.employee_service.dto.employee.UpdateEmployeeDTO;
import com.ems.employee_service.entity.Department;
import com.ems.employee_service.entity.Employee;
import com.ems.employee_service.enums.Role;
import com.ems.employee_service.enums.Status;
import com.ems.employee_service.feign.AuthServiceClient;
import com.ems.employee_service.repository.EmployeeRepository;
import com.ems.employee_service.utils.exceptions.ResourceNotFoundException;
import com.ems.employee_service.utils.exceptions.UniqueConstraintViolationException;
import com.ems.employee_service.utils.pagination.PaginationQueryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentService departmentService;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department(1L, "IT", "IT Department", new ArrayList<>());
        employee = new Employee(1L, "John", "Doe", "john.doe@example.com", Status.FULL_TIME, "2024-01-01", "2024-01-01", department);
    }

    @Test
    void createFirstAdminEmployee_DuplicateEmail() {
        CreateFirstAdminDTO dto = new CreateFirstAdminDTO("john.doe@example.com", "John", "Doe");
        when(departmentService.createDepartment(any(CreateDepartmentDTO.class))).thenReturn(department);
        when(employeeRepository.save(any(Employee.class))).thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint (email)"));

        assertThrows(UniqueConstraintViolationException.class, () -> employeeService.createFirstAdminEmployee(dto));
    }

    @Test
    void createEmployee_Success() {
        CreateEmployeeDTO dto = new CreateEmployeeDTO("John", "Doe", "john.doe@example.com", Role.ADMIN, Status.FULL_TIME, 1L);
        when(departmentService.findById(dto.departmentId())).thenReturn(department);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResDTO result = employeeService.createEmployee(dto);

        assertNotNull(result);
        assertEquals(employee.getEmail(), result.getEmail());
        verify(authServiceClient, times(1)).createEmployeeAuth(any(CreateEmployeeAuthDTO.class));
    }

    @Test
    void updateEmployee_Success() {
        UpdateEmployeeDTO dto = new UpdateEmployeeDTO(Role.ADMIN, 2L, Status.PART_TIME);
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        when(departmentService.findById(dto.departmentId())).thenReturn(new Department());
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Map<String, String> result = employeeService.updateEmployee(dto, 1L);

        assertTrue(result.containsKey("status"));
        assertTrue(result.containsKey("departmentId"));
        verify(authServiceClient, times(1)).updateEmployeeRole(anyString(), anyLong());
    }

    @Test
    void deleteEmployee_Success() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        doNothing().when(authServiceClient).deleteAuthAccount(anyLong());
        doNothing().when(employeeRepository).delete(any(Employee.class));

        assertDoesNotThrow(() -> employeeService.deleteEmployee(1L));
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void getEmployeeById_Success() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));

        EmployeeResDTO result = employeeService.getEmployeeById(1L);
        assertNotNull(result);
        assertEquals(employee.getEmail(), result.getEmail());
    }

    @Test
    void getEmployeeById_NotFound() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(1L));
    }

    @Test
    void getAllEmployees_Success() {
        PaginationQueryDTO pagination = new PaginationQueryDTO();
        Page<Employee> page = new PageImpl<>(Collections.singletonList(employee));
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<EmployeeResDTO> result = employeeService.getAllEmployees(pagination);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getEmployeesByDepartment_Success() {
        PaginationQueryDTO pagination = new PaginationQueryDTO();
        Page<Employee> page = new PageImpl<>(Collections.singletonList(employee));
        when(employeeRepository.findByDepartmentId(anyLong(), any(Pageable.class))).thenReturn(page);

        Page<EmployeeResDTO> result = employeeService.getEmployeesByDepartment(1L, pagination);

        assertEquals(1, result.getTotalElements());
    }
}
