package com.ems.auth_service.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {
    @Email(message = "Email address is required")
    public String email;

    @NotBlank(message = "Password is required")
    public String password;
}
