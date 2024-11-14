package com.ioob.backend.domain.kanban.service;

import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.auth.repository.UserRepository;
import com.ioob.backend.domain.kanban.dto.AssignRoleDto;
import com.ioob.backend.domain.kanban.dto.ProjectRequestDto;
import com.ioob.backend.domain.kanban.dto.ProjectResponseDto;
import com.ioob.backend.domain.kanban.dto.UserProjectRoleDto;
import com.ioob.backend.domain.kanban.entity.Project;
import com.ioob.backend.domain.kanban.entity.Role;
import com.ioob.backend.domain.kanban.entity.UserProjectRole;
import com.ioob.backend.domain.kanban.repository.ProjectRepository;
import com.ioob.backend.domain.kanban.repository.UserProjectRoleRepository;
import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public ProjectResponseDto createProject(User user, ProjectRequestDto projectRequestDto) {
        Project project = Project.builder()
                .name(projectRequestDto.getName())
                .description(projectRequestDto.getDescription())
                .build();
        project = projectRepository.save(project);

        UserProjectRole userProjectRole = UserProjectRole.builder()
                .user(user)
                .project(project)
                .role(Role.ROLE_PROJECT_ADMIN)
                .build();
        userProjectRoleRepository.save(userProjectRole);

        return ProjectResponseDto.of(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectById(User user, Long projectId) {
        Project project = findProjectById(projectId);
        return ProjectResponseDto.of(project);
    }

    @Transactional(readOnly = true)
    public List<UserProjectRoleDto> getUsersByProjectId(User user, Long projectId) {
        return userProjectRoleRepository.findByProjectId(projectId).stream()
                .map(UserProjectRoleDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectResponseDto updateProject(User user, Long projectId, ProjectRequestDto projectRequestDto) {
        checkProjectAdminPermission(user, projectId);

        Project project = findProjectById(projectId);

        project.setName(projectRequestDto.getName());
        project.setDescription(projectRequestDto.getDescription());
        return ProjectResponseDto.of(project);
    }

    @Transactional
    public void deleteProject(User user, Long projectId) {
        checkProjectAdminPermission(user, projectId);

        Project project = findProjectById(projectId);
        projectRepository.delete(project);
    }

    @Transactional
    public void assignRoleToUser(User user, Long projectId, AssignRoleDto dto) {
        Project project = findProjectById(projectId);

        checkProjectAdminPermission(user, projectId);

        User assignedUser = userRepository.findByEmail(dto.getUserEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 권한 부여
        UserProjectRole userProjectRole = UserProjectRole.builder()
                .user(assignedUser)
                .project(project)
                .role(dto.getRole())
                .build();

        userProjectRoleRepository.save(userProjectRole);
    }

    private Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    private void checkProjectAdminPermission(User user, Long projectId) {
        if (user.isAdmin()) {
            return;
        }

        UserProjectRole userProjectRole = userProjectRoleRepository.findByUserEmailAndProjectId(user.getEmail(), projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERMISSION_DENIED));

        if (userProjectRole.getRole() != Role.ROLE_PROJECT_ADMIN) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
    }
}
