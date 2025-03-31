package com.ems.employee_service.service;

import com.ems.employee_service.dto.department.CreateDepartmentDTO;
import com.ems.employee_service.dto.department.DepartmentResDTO;
import com.ems.employee_service.dto.department.UpdateDepartmentDTO;
import com.ems.employee_service.entity.Department;
import com.ems.employee_service.repository.DepartmentRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository repo;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;
    private CreateDepartmentDTO createDepartmentDTO;
    private UpdateDepartmentDTO updateDepartmentDTO;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .name("engineering")
                .description("Engineering Department")
                .build();

        createDepartmentDTO = new CreateDepartmentDTO("Engineering", "Engineering Department");
        updateDepartmentDTO = new UpdateDepartmentDTO("HR", "Human Resources");
    }

    @Test
    void createDepartment_Success() {
        when(repo.save(any(Department.class))).thenReturn(department);

        Department created = departmentService.createDepartment(createDepartmentDTO);

        assertNotNull(created);
        assertEquals("engineering", created.getName());
        verify(repo, times(1)).save(any(Department.class));
    }

    @Test
    void createDepartment_UniqueConstraintViolation() {
        when(repo.save(any(Department.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key value violates unique constraint (name)"));

        assertThrows(UniqueConstraintViolationException.class, () -> departmentService.createDepartment(createDepartmentDTO));
    }

    @Test
    void updateDepartment_Success() {
        when(repo.findById(1L)).thenReturn(Optional.of(department));
        when(repo.save(any(Department.class))).thenReturn(department);

        Map<String, String> updated = departmentService.updateDepartment(updateDepartmentDTO, 1L);

        assertEquals("HR", updated.get("name"));
        assertEquals("Human Resources", updated.get("description"));
        verify(repo, times(1)).save(department);
    }

    @Test
    void updateDepartment_NotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.updateDepartment(updateDepartmentDTO, 1L));
    }

    @Test
    void deleteDepartment_NotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.deleteDepartment(1L));
    }

    @Test
    void getDepartmentById_Success() {
        when(repo.findById(1L)).thenReturn(Optional.of(department));

        DepartmentResDTO dto = departmentService.getDepartmentById(1L);

        assertNotNull(dto);
        assertEquals("engineering", dto.getName());
    }

    @Test
    void getDepartmentById_NotFound() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.getDepartmentById(1L));
    }

    @Test
    void getAllDepartments_Success() {
        PaginationQueryDTO paginationQueryDTO = new PaginationQueryDTO();
        paginationQueryDTO.setPage("1");
        paginationQueryDTO.setPerPage("10");

        Pageable pageable = PageRequest.of(paginationQueryDTO.getPage() - 1, paginationQueryDTO.getPerPage());
        Page<Department> departmentPage = new PageImpl<>(List.of(department));

        when(repo.findAll(pageable)).thenReturn(departmentPage);

        Page<DepartmentResDTO> result = departmentService.getAllDepartments(paginationQueryDTO);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
}
