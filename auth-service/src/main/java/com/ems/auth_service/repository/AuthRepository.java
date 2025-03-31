package com.ems.auth_service.repository;

import com.ems.auth_service.entity.Auth;
import com.ems.auth_service.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<Auth, UUID> {
    Optional<Auth> findFirstByRole(Role role);
    Optional<Auth> findByEmail(String email);
    Optional<Auth> findByEmployeeId(long employeeId);
}
