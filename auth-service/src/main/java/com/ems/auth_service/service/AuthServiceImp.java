package com.ems.auth_service.service;

import com.ems.auth_service.config.rabbitmq.RabbitMQConfig;
import com.ems.auth_service.dto.jwt.TokenDetailDTO;
import com.ems.auth_service.dto.user.*;
import com.ems.auth_service.entity.Auth;
import com.ems.auth_service.enums.Role;
import com.ems.auth_service.feign.EmployeeServiceClient;
import com.ems.auth_service.repository.AuthRepository;
import com.ems.auth_service.utils.exceptions.*;
import com.ems.auth_service.utils.helpers.Helpers;
import com.ems.auth_service.utils.jwtUtils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {
    private final AuthRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final EmployeeServiceClient employeeServiceClient;

    @Value("${jwt.refreshTokenTime}")
    private long refreshTokenTime;

    //    This is used for creating only the first admin account
    @Override
    @Transactional
    public void createAdminAccount(CreateAdminDTO createAdminDTO) {
        if (repo.findFirstByRole(Role.ADMIN).isPresent()) {
            throw new UnprocessableEntityException("Can only create the first admin");
        }

        Map<String, String> employeeData;
        employeeData = employeeServiceClient.createFirstAdminEmployee(
                new CreateFirstAdminDTO(createAdminDTO.getFirstName(), createAdminDTO.getLastName(), createAdminDTO.getEmail())
        );
//        try {
//            employeeData = employeeServiceClient.createFirstAdminEmployee(
//                    new CreateFirstAdminDTO(createAdminDTO.getFirstName(), createAdminDTO.getLastName(), createAdminDTO.getEmail())
//            );
//        } catch (Exception e) {
//            throw new UnprocessableEntityException("Unable to create admin Account");
//        }

        Auth auth = Auth.builder()
                .email(createAdminDTO.getEmail())
                .employeeId(Long.parseLong(employeeData.get("employeeId")))
                .password(passwordEncoder.encode(createAdminDTO.password))
                .role(Role.ADMIN)
                .build();

        try {
            repo.save(auth);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("duplicate key") && e.getMessage().contains("(email)")) {
                throw new UniqueConstraintViolationException("Account already exist");
            }

            throw new InternalServerException("Internal server error", e);
        }
    }

    @Override
    @Transactional
    public Auth createUser(CreateEmployeeAuthDTO createUserDTO, String password) {
        Auth auth = Auth.builder()
                .email(createUserDTO.getEmail())
                .employeeId(createUserDTO.getEmployeeId())
                .password(passwordEncoder.encode(password))
                .role(createUserDTO.getRole())
                .build();

        try {
            return repo.save(auth);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("duplicate key") && e.getMessage().contains("(email)")) {
                throw new UniqueConstraintViolationException("Account already exist");
            }

            throw new InternalServerException("Internal server error", e);
        }
    }

    @Override
    public Auth setResendPassword(String randomPassword, String email) {
        Auth auth = repo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        auth.setPassword(passwordEncoder.encode(randomPassword));
        return repo.save(auth);
    }

    @Override
    public LoginResponseDTO loginEmployee(LoginDTO loginDTO) {
        Auth loggedInAuth = repo.findByEmail(loginDTO.email)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), loggedInAuth.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        Map<String, TokenDetailDTO> credential = jwtUtil.getCredentials(
                jwtUtil.generateUserClaim(loggedInAuth), loggedInAuth.getId().toString());
        return new LoginResponseDTO(loggedInAuth, credential);
    }

    @Override
    public void publishCreateEmployeeNotifications(String email, String firstName, String password) {
        Map<String, Object> message = new HashMap<>();
        message.put("email", email);
        message.put("firstName", firstName);
        message.put("password", password);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE, RabbitMQConfig.EMPLOYEE_NOTIFICATION_ROUTING_KEY, message);
    }

    @Override
    public Map<String, TokenDetailDTO> generateNewTokens(RefreshTokenDTO refreshTokenDTO) {
        Map<String, Object> claim = jwtUtil.getRefreshJwtClaim(refreshTokenDTO.refreshToken());
        blackListRefreshToken(refreshTokenDTO.refreshToken());
        Auth auth = this.getById(UUID.fromString((String) claim.get("userId")));
        return jwtUtil.getCredentials(
                jwtUtil.generateUserClaim(auth), auth.getId().toString());
    }

    @Override
    public void logout(TokensDTO tokensDTO) {
        try {
            blackListAccessToken(tokensDTO.accessToken());
            blackListRefreshToken(tokensDTO.refreshToken());
        } catch (MalformedJwtException e) {
            throw new UnauthorizedException("Invalid JWT format: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("JWT token has expired");
        } catch (UnsupportedJwtException e) {
            throw new UnauthorizedException("Unsupported JWT token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("JWT claims string is empty or invalid");
        } catch (JwtException e) {
            throw new UnauthorizedException("JWT processing failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateEmployeeRole(Role role, long employeeId) {
        Auth auth = repo.findByEmployeeId(employeeId).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        auth.setRole(role);
        repo.save(auth);
    }

    @Override
    public Auth getById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public void deleteAuthAccount(Long employeeId) {
        Auth employee = repo.findByEmployeeId(employeeId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        repo.delete(employee);
    }

    private void blackListAccessToken(String accessToken) {
        Map<String, Object> claim = jwtUtil.getJwtClaim(accessToken);
        String key = Helpers.getAccessTokenBlackListKey((String) claim.get("jti"));

        if (redisTemplate.opsForValue().get(key) != null) {
            throw new UnauthorizedException("Invalid accessToken token");
        }

        blacklistToken(key);
    }

    private void blackListRefreshToken(String refreshToken) {
        Map<String, Object> claim = jwtUtil.getRefreshJwtClaim(refreshToken);
        String key = Helpers.getRefreshTokenBlackListKey((String) claim.get("jti"));

        if (redisTemplate.opsForValue().get(key) != null) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        blacklistToken(key);
    }

    private void blacklistToken(String key) {
        redisTemplate.opsForValue().set(key, "revoked", refreshTokenTime, TimeUnit.MINUTES);
    }
}
