package com.ioob.backend.domain.kanban.service;

import com.ioob.backend.domain.kanban.dto.ProjectRequestDto;
import com.ioob.backend.domain.kanban.dto.ProjectResponseDto;
import com.ioob.backend.domain.kanban.dto.UserProjectRoleDto;
import com.ioob.backend.domain.kanban.entity.Project;
import com.ioob.backend.domain.kanban.entity.Role;
import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.kanban.entity.UserProjectRole;
import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import com.ioob.backend.domain.kanban.repository.ProjectRepository;
import com.ioob.backend.domain.kanban.repository.UserProjectRoleRepository;
import com.ioob.backend.domain.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserProjectRoleRepository userProjectRoleRepository;
    private final RoleService roleService;

    
    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectResponseDto::new)
                .collect(Collectors.toList());
    }

    
    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectById(User user, Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        // 프로젝트 권한 확인
        if (!user.isAdmin() && !roleService.isUserInProject(id, user.getEmail())) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }

        return new ProjectResponseDto(project);
    }

    
    @Transactional
    public ProjectResponseDto createProject(User user, ProjectRequestDto projectRequestDto) {
        // 프로젝트 생성
        Project project = Project.builder()
                .name(projectRequestDto.getName())
                .description(projectRequestDto.getDescription())
                .build();
        project = projectRepository.save(project);

        // 현재 로그인한 사용자에게 프로젝트 관리자 권한 부여
        UserProjectRole userProjectRole = UserProjectRole.builder()
                .user(user)
                .project(project)
                .role(Role.ROLE_PROJECT_ADMIN)
                .build();
        userProjectRoleRepository.save(userProjectRole);

        return new ProjectResponseDto(project);
    }

    
    @Transactional
    public ProjectResponseDto updateProject(Long id, ProjectRequestDto projectRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        // 관리자 권한 확인
        if (!roleService.hasPermission(id, Role.ROLE_PROJECT_ADMIN, email)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);
        }

        project.setName(projectRequestDto.getName());
        project.setDescription(projectRequestDto.getDescription());
        return new ProjectResponseDto(project);
    }

    
    @Transactional
    public void deleteProject(User user, Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        // 관리자 권한 확인
        if (!user.isAdmin() && !roleService.hasPermission(id, Role.ROLE_PROJECT_ADMIN, user.getEmail())) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);
        }

        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getUserProjects(String email) {
        List<UserProjectRole> userProjectRoles = userProjectRoleRepository.findByUserEmail(email);

        return userProjectRoles.stream()
                .map(userProjectRole -> new ProjectResponseDto(userProjectRole.getProject()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    
    public List<UserProjectRoleDto> getUsersByProjectId(User user, Long projectId) {

        // 프로젝트 접근 권한 확인
        if (!user.isAdmin() && !roleService.isUserInProject(projectId, user.getEmail())) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);  // 권한 없음
        }

        return userProjectRoleRepository.findByProjectId(projectId).stream()
                .map(UserProjectRoleDto::from)
                .collect(Collectors.toList());
    }
}
