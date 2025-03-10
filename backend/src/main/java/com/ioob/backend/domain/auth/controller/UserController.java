package com.ioob.backend.domain.auth.controller;

import com.ioob.backend.domain.auth.dto.UserResponseDto;
import com.ioob.backend.domain.auth.service.UserService;
import com.ioob.backend.domain.kanban.dto.ProjectResponseDto;
import com.ioob.backend.domain.kanban.dto.TaskResponseDto;
import com.ioob.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "User", description = "사용자 전용 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "프로필 조회", description = "현재 로그인한 사용자의 프로필을 조회하는 API")
    @GetMapping("/profile")
    public UserResponseDto getUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUserProfile(userDetails.getUser());
    }

    @Operation(summary = "사용자의 테스크 목록 조회", description = "로그인한 사용자가 할당된 모든 테스크 목록을 조회하는 API")
    @GetMapping("/my-tasks")
    public List<TaskResponseDto> getUserTasks(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUserTasks(userDetails.getUserId());
    }

    @Operation(summary = "사용자의 프로젝트 목록 조회", description = "로그인한 사용자가 속한 모든 프로젝트 목록을 조회하는 API")
    @GetMapping("/my-projects")
    public List<ProjectResponseDto> getUserProjects(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getUserProjects(userDetails.getUserId());
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자가 회원 탈퇴를 요청할 때 사용하는 API")
    @PostMapping("/delete")
    public void deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteUser(userDetails.getUser());
    }
}
