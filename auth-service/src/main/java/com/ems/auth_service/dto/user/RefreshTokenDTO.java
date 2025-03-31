package com.ems.auth_service.dto.user;

import com.ems.auth_service.utils.customValidators.jwtFormatValidator.ValidJwtFormat;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDTO(@NotBlank @ValidJwtFormat String refreshToken) {
}
