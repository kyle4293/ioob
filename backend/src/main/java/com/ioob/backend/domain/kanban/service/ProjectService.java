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
        return new ProjectResponseDto(project);
    }

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

        return new ProjectResponseDto(project);
    }

    @Transactional
    public ProjectResponseDto updateProject(Long id, ProjectRequestDto projectRequestDto) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        project.setName(projectRequestDto.getName());
        project.setDescription(projectRequestDto.getDescription());
        return new ProjectResponseDto(project);
    }

    @Transactional
    public void deleteProject(User user, Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getUserProjects(Long userId) {
        List<UserProjectRole> userProjectRoles = userProjectRoleRepository.findByUserId(userId);
        return userProjectRoles.stream()
                .map(userProjectRole -> new ProjectResponseDto(userProjectRole.getProject()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserProjectRoleDto> getUsersByProjectId(User user, Long projectId) {
        return userProjectRoleRepository.findByProjectId(projectId).stream()
                .map(UserProjectRoleDto::from)
                .collect(Collectors.toList());
    }
}
