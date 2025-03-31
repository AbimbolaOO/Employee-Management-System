package com.ems.employee_service.service;

import com.ems.employee_service.dto.department.CreateDepartmentDTO;
import com.ems.employee_service.dto.department.DepartmentResDTO;
import com.ems.employee_service.dto.department.UpdateDepartmentDTO;
import com.ems.employee_service.entity.Department;
import com.ems.employee_service.repository.DepartmentRepository;
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
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository repo;

    @Override
    public Department createDepartment(CreateDepartmentDTO createDepartmentDTO) {
        Department department = Department.builder()
                .name(createDepartmentDTO.name().toLowerCase())
                .description(createDepartmentDTO.description())
                .build();

        try {
            return repo.save(department);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("duplicate key") && e.getMessage().contains("(name)")) {
                throw new UniqueConstraintViolationException("Department already exist");
            }

            throw new InternalServerException("Internal server error", e);
        }
    }

    @Override
    public Map<String, String> updateDepartment(UpdateDepartmentDTO updateDepartmentDTO, Long departmentId) {
        Department department = this.findById(departmentId);

        Map<String, String> responseObj = new HashMap<>();

        if (updateDepartmentDTO.name() != null) {
            department.setName(updateDepartmentDTO.name().toLowerCase());
            responseObj.put("name", updateDepartmentDTO.name());
        }
        if (updateDepartmentDTO.description() != null) {
            department.setDescription(updateDepartmentDTO.description());
            responseObj.put("description", updateDepartmentDTO.description());
        }

        repo.save(department);
        return responseObj;
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = this.findById(id);
        department.getEmployee().forEach(employee -> employee.setDepartment(null));
        repo.delete(department);
    }

    @Override
    public DepartmentResDTO getDepartmentById(Long id) {
        return repo.findById(id).map(DepartmentResDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }

    @Override
    public Department findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Department not found"));
    }

    @Override
    public Page<DepartmentResDTO> getAllDepartments(PaginationQueryDTO paginationQueryDTO) {
        int page = paginationQueryDTO.getPage() - 1;
        int size = paginationQueryDTO.getPerPage();
        Pageable pageable = PageRequest.of(page, size);

        return repo.findAll(pageable).map(DepartmentResDTO::new);
    }
}
