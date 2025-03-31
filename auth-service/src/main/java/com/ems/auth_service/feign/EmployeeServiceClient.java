package com.ems.auth_service.feign;

import com.ems.auth_service.dto.user.CreateFirstAdminDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "employee-service")
public interface EmployeeServiceClient {

    @PostMapping("/api/employee-service/employees/first-admin")
    Map<String, String> createFirstAdminEmployee(@RequestBody CreateFirstAdminDTO createFirstAdminDTO);
}
