package com.ems.auth_service.dto.user;

import com.ems.auth_service.utils.customValidators.jwtFormatValidator.ValidJwtFormat;
import jakarta.validation.constraints.NotBlank;


public record TokensDTO(@NotBlank(message = "Refresh token is required") @ValidJwtFormat String refreshToken,
                        @NotBlank(message = "Access token is required") @ValidJwtFormat String accessToken) {
}
