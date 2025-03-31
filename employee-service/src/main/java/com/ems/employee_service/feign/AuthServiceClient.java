package com.ems.employee_service.feign;

import com.ems.employee_service.dto.auth.CreateEmployeeAuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {
    @PostMapping("/api/auth-service/auth/employee")
    void createEmployeeAuth(@RequestBody CreateEmployeeAuthDTO createEmployeeAuthDTO);

    @PostMapping("/api/auth-service/auth/role/{role}/{employeeId}")
    void updateEmployeeRole(@PathVariable("role") String role, @PathVariable("employeeId") long employeeId);

    @DeleteMapping("/api/auth-service/auth/{employeeId}")
    void deleteAuthAccount(@PathVariable("employeeId") Long employeeId);
}
