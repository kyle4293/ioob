package com.ioob.backend.controller;

import com.ioob.backend.dto.ProjectResponseDto;
import com.ioob.backend.dto.TaskResponseDto;
import com.ioob.backend.dto.UserProfileResponseDto;
import com.ioob.backend.response.ApiResponse;
import com.ioob.backend.service.ProjectService;
import com.ioob.backend.service.TaskService;
import com.ioob.backend.security.UserDetailsImpl;
import com.ioob.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "사용자 전용 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProjectService projectService;
    private final TaskService taskService;

    @Operation(summary = "사용자의 프로젝트 목록 조회", description = "로그인한 사용자가 속한 모든 프로젝트 목록을 조회하는 API")
    @GetMapping("/my-projects")
    public List<ProjectResponseDto> getUserProjects(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return projectService.getUserProjects(userDetails.getUser().getEmail());
    }

    @Operation(summary = "사용자의 테스크 목록 조회", description = "로그인한 사용자가 할당된 모든 테스크 목록을 조회하는 API")
    @GetMapping("/my-tasks")
    public List<TaskResponseDto> getUserTasks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return taskService.getUserTasks(userDetails.getUser().getId());
    }

    @Operation(
            summary = "프로필 조회", description = "현재 로그인한 사용자의 프로필을 조회하는 API")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponseDto>> getUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            UserProfileResponseDto profile = userService.getUserProfile(userDetails.getUser());
            return ResponseEntity.ok(ApiResponse.success("200", "프로필 조회 성공", profile));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.failure("400", "프로필 조회 실패"));
        }
    }
}
