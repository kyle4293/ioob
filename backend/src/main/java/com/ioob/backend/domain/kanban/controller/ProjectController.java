package com.ioob.backend.controller;

import com.ioob.backend.dto.ProjectRequestDto;
import com.ioob.backend.dto.ProjectResponseDto;
import com.ioob.backend.dto.UserProjectRoleDto;
import com.ioob.backend.entity.RoleName;
import com.ioob.backend.security.UserDetailsImpl;
import com.ioob.backend.service.ProjectService;
import com.ioob.backend.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    private final RoleService roleService;


    @Operation(summary = "프로젝트 목록 조회", description = "모든 프로젝트를 조회하는 API")
    @GetMapping
    public List<ProjectResponseDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @Operation(summary = "프로젝트 상세 조회", description = "ID를 통해 특정 프로젝트를 조회하는 API")
    @GetMapping("/{id}")
    public ProjectResponseDto getProjectById(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return projectService.getProjectById(userDetails.getUsername(), id);
    }

    @Operation(summary = "프로젝트 생성", description = "새로운 프로젝트를 생성하는 API")
    @PostMapping
    public ProjectResponseDto createProject(@RequestBody ProjectRequestDto projectRequestDto) {
        return projectService.createProject(projectRequestDto);
    }

    @Operation(summary = "프로젝트 삭제", description = "ID를 통해 특정 프로젝트를 삭제하는 API")
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @Operation(summary = "프로젝트 권한 부여", description = "프로젝트 관리자만 권한을 부여할 수 있습니다.",
    parameters = {
        @Parameter(name = "projectId", description = "권한을 부여할 프로젝트 ID", example = "1"),
        @Parameter(name = "userEmail", description = "권한을 부여할 사용자 Email", example = "wwkyle01@gmail.com"),
        @Parameter(name = "roleName", description = "부여할 권한 (ROLE_PROJECT_ADMIN 또는 ROLE_USER)", example = "ROLE_USER")
    })
    @PostMapping("/{projectId}/assign-role")
    public void assignRoleToUser(
            @PathVariable Long projectId,
            @RequestParam String userEmail,
            @RequestParam RoleName roleName) {

        roleService.assignRoleToUser(projectId, userEmail, roleName);
    }

    @Operation(summary = "프로젝트에 속한 사용자들 조회", description = "프로젝트에 속한 모든 사용자들을 조회하는 API")
    @GetMapping("/{projectId}/users")
    public List<UserProjectRoleDto> getUsersByProjectId(@PathVariable Long projectId) {
        return projectService.getUsersByProjectId(projectId);
    }
}
