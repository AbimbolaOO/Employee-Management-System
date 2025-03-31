package com.ems.auth_service.service;

import com.ems.auth_service.dto.jwt.TokenDetailDTO;
import com.ems.auth_service.dto.user.*;
import com.ems.auth_service.entity.Auth;
import com.ems.auth_service.enums.Role;

import java.util.Map;
import java.util.UUID;

public interface AuthService {

    void createAdminAccount(CreateAdminDTO createAdminDTO);

    Auth createUser(CreateEmployeeAuthDTO createUserDTO, String password);

    Auth setResendPassword(String randomPassword, String email);

    LoginResponseDTO loginEmployee(LoginDTO loginDTO);

    void publishCreateEmployeeNotifications(String email, String firstName, String password);

    Map<String, TokenDetailDTO> generateNewTokens(RefreshTokenDTO refreshTokenDTO);

    void updateEmployeeRole(Role role, long employeeId);

    void logout(TokensDTO tokensDTO);

    Auth getById(UUID id);

    void deleteAuthAccount(Long employeeId);
}
