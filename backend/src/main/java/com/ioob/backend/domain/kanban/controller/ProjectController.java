package com.ioob.backend.domain.kanban.controller;

import com.ioob.backend.domain.kanban.dto.AssignRoleDto;
import com.ioob.backend.domain.kanban.dto.ProjectRequestDto;
import com.ioob.backend.domain.kanban.dto.ProjectResponseDto;
import com.ioob.backend.domain.kanban.dto.UserProjectRoleDto;
import com.ioob.backend.domain.kanban.service.ProjectService;
import com.ioob.backend.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Project Management", description = "프로젝트 관련 CRUD API")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성하는 API")
    @PostMapping
    public ProjectResponseDto createProject(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ProjectRequestDto projectRequestDto) {
        return projectService.createProject(userDetails.getUser(), projectRequestDto);
    }

    @Operation(summary = "프로젝트 목록 조회", description = "모든 프로젝트를 조회하는 API")
    @GetMapping
    public List<ProjectResponseDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @Operation(summary = "프로젝트 상세 조회", description = "ID를 통해 특정 프로젝트를 조회하는 API")
    @GetMapping("/{projectId}")
    public ProjectResponseDto getProjectById(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long projectId) {
        return projectService.getProjectById(userDetails.getUser(), projectId);
    }

    @Operation(summary = "프로젝트에 속한 사용자들 조회", description = "프로젝트에 속한 모든 사용자들을 조회하는 API")
    @GetMapping("/{projectId}/users")
    public List<UserProjectRoleDto> getUsersByProjectId(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long projectId) {
        return projectService.getUsersByProjectId(userDetails.getUser(), projectId);
    }

    @Operation(summary = "프로젝트 삭제", description = "ID를 통해 특정 프로젝트를 삭제하는 API")
    @DeleteMapping("/{projectId}")
    public void deleteProject(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long projectId) {
        projectService.deleteProject(userDetails.getUser(), projectId);
    }

    @Operation(summary = "프로젝트 권한 부여", description = "프로젝트 관리자만 권한을 부여할 수 있습니다.")
    @PostMapping("/{projectId}/assign-role")
    public void assignRoleToUser(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @PathVariable Long projectId,
                                 @RequestBody AssignRoleDto dto) {

        projectService.assignRoleToUser(userDetails.getUser(), projectId, dto);
    }
}
