package com.ems.auth_service.controller;

import com.ems.auth_service.dto.jwt.TokenDetailDTO;
import com.ems.auth_service.dto.user.*;
import com.ems.auth_service.entity.Auth;
import com.ems.auth_service.enums.Role;
import com.ems.auth_service.service.AuthService;
import com.ems.auth_service.utils.helpers.Helpers;
import com.ems.auth_service.utils.responseBody.ResponseBody;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("api/auth-service/auth")
@RequiredArgsConstructor
@Tag(name = "Employee Authentication", description = "Employee authentication credentials")
public class AuthController {
    private final AuthService authService;

    @PostMapping("signup-admin")
    @Operation(summary = "Creates admin account",
            description = "Helps create the very first admin account. Can only be used once for the first admin account")
    public ResponseEntity<ResponseBody<Object>> signup(@Valid @RequestBody CreateAdminDTO createAdminDTO) {
        this.authService.createAdminAccount(createAdminDTO);
        return ResponseEntity.ok(ResponseBody.success("Admin account created"));
    }

    @PostMapping("resend-password")
    @Operation(summary = "Resend employee password", description = "Use to resend employee password")
    public ResponseEntity<ResponseBody<Object>> resendOtp(@Valid @RequestBody CreateEmployeeAuthDTO createEmployeeAuthDTO) {
        String randPassword = Helpers.generatePassword(8);
        Auth auth = this.authService.setResendPassword(randPassword, createEmployeeAuthDTO.email);
        this.authService.publishCreateEmployeeNotifications(auth.getEmail(), createEmployeeAuthDTO.getFirstName(), randPassword);
        return ResponseEntity.ok(ResponseBody.success("Password Resent"));
    }

    @PostMapping("signin")
    @Operation(summary = "Sign in employee", description = "Used to sign in employee")
    public ResponseEntity<ResponseBody<LoginResponseDTO>> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginResponseDTO loginEmployee = this.authService.loginEmployee(loginDTO);
        return ResponseEntity.ok(ResponseBody.success("Successful signin", loginEmployee));
    }

    @PostMapping("refresh-token")
    @Operation(summary = "Get access token", description = "Get new access and new refresh token for a signin employee")
    public ResponseEntity<ResponseBody<Map<String, TokenDetailDTO>>> refreshToken(
            @Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        Map<String, TokenDetailDTO> credentials = this.authService.generateNewTokens(refreshTokenDTO);
        return ResponseEntity.ok(ResponseBody.success("New tokens", credentials));
    }

    @PostMapping("logout")
    @Operation(summary = "Logout user", description = "For login out of a signed in employee account")
    public ResponseEntity<Void> logoutUser(@Valid @RequestBody TokensDTO tokensDTO) {
        this.authService.logout(tokensDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("employee")
    @Hidden
    public ResponseEntity<Void> createEmployeeAuth(@RequestBody CreateEmployeeAuthDTO createUserDTO) {
        String randPassword = Helpers.generatePassword(8);
        Auth auth = this.authService.createUser(createUserDTO, randPassword);
        this.authService.publishCreateEmployeeNotifications(auth.getEmail(), createUserDTO.getFirstName(), randPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("role/{role}/{employeeId}")
    @Hidden
    public ResponseEntity<Void> updateEmployeeRole(@PathVariable("role") String role,
                                                   @PathVariable("employeeId") long employeeId) {
        this.authService.updateEmployeeRole(Role.valueOf(role), employeeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{employeeId}")
    @Hidden
    public ResponseEntity<Void> deleteAuthAccount(@PathVariable("employeeId") Long employeeId) {
        this.authService.deleteAuthAccount(employeeId);
        return ResponseEntity.noContent().build();
    }
}
