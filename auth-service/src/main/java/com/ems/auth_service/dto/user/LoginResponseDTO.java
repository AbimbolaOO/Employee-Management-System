package com.ems.auth_service.dto.user;

import com.ems.auth_service.dto.jwt.TokenDetailDTO;
import com.ems.auth_service.entity.Auth;
import com.ems.auth_service.enums.Role;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class LoginResponseDTO {
    private UUID id;

    private String email;

    private long employeeId;

    private Role role;

    private String createdAt;

    private String updatedAt;

    private Map<String, TokenDetailDTO> credentials;

    public LoginResponseDTO(Auth auth, Map<String, TokenDetailDTO> credentials) {
        this.id = auth.getId();
        this.email = auth.getEmail();
        this.employeeId = auth.getEmployeeId();
        this.role = auth.getRole();
        this.createdAt = auth.getCreatedAt();
        this.updatedAt = auth.getUpdatedAt();
        this.credentials = credentials;
    }
}
