package com.ioob.backend.domain.admin.controller;

import com.ioob.backend.domain.admin.service.AdminService;
import com.ioob.backend.domain.auth.dto.UserInfoDto;
import com.ioob.backend.domain.auth.repository.VerificationTokenRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin", description = "관리자 전용 API")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final VerificationTokenRepository repository;

    @Operation(summary = "모든 사용자 조회", description = "모든 사용자를 조회하는 관리자 전용 API")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public List<UserInfoDto> getAllUsers() {
        return adminService.getAllUsers();
    }

    @Operation(summary = "사용자 조회", description = "사용자를 조회하는 관리자 전용 API")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users/{id}")
    public UserInfoDto getUsers(@PathVariable Long id) {
        return adminService.getUser(id);
    }

    @Operation(summary = "사용자 삭제", description = "ID를 통해 사용자를 삭제하는 관리자 전용 API")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        adminService.deleteUserById(id);
    }

}
