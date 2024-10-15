package com.ioob.backend.domain.admin;

import com.ioob.backend.domain.auth.dto.UserInfoDto;
import com.ioob.backend.domain.auth.dto.UserProfileResponseDto;
import com.ioob.backend.domain.auth.entity.VerificationToken;
import com.ioob.backend.domain.auth.repository.VerificationTokenRepository;
import com.ioob.backend.domain.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "BackOffice", description = "관리자 전용 백오피스 API")
@RestController
@RequestMapping("/api/backoffice")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final VerificationTokenRepository repository;

    @Operation(summary = "모든 사용자 조회", description = "모든 사용자를 조회하는 관리자 전용 API")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public List<UserInfoDto> getAllUsers() {
        return userService.getAllUsers();
    }


    @Operation(summary = "사용자 삭제", description = "ID를 통해 사용자를 삭제하는 관리자 전용 API")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @GetMapping("/token")
    public List<VerificationToken> getTokens() {
        return repository.findAll();
    }
}
