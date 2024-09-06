package com.ioob.backend.service.impl;

import com.ioob.backend.dto.ProjectRequestDto;
import com.ioob.backend.dto.ProjectResponseDto;
import com.ioob.backend.entity.Project;
import com.ioob.backend.entity.RoleName;
import com.ioob.backend.entity.User;
import com.ioob.backend.entity.UserProjectRole;
import com.ioob.backend.exception.CustomException;
import com.ioob.backend.exception.ErrorCode;
import com.ioob.backend.repository.ProjectRepository;
import com.ioob.backend.repository.RoleRepository;
import com.ioob.backend.repository.UserRepository;
import com.ioob.backend.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(ProjectResponseDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDto getProjectById(String email, Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        // 사용자의 프로젝트 권한 확인
        List<UserProjectRole> roles = roleRepository.findByUserEmailAndProjectId(email, id);
        if (roles.isEmpty()) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);  // 권한 없음
        }

        return new ProjectResponseDto(project);
    }

    @Override
    @Transactional
    public ProjectResponseDto createProject(ProjectRequestDto projectRequestDto) {
        // 프로젝트 생성
        Project project = Project.builder()
                .name(projectRequestDto.getName())
                .description(projectRequestDto.getDescription())
                .build();

        project = projectRepository.save(project);

        // 현재 로그인한 사용자를 가져옴
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 프로젝트 관리자 역할을 부여
        UserProjectRole projectAdminRole = UserProjectRole.builder()
                .user(user)
                .project(project)
                .role(RoleName.ROLE_PROJECT_ADMIN)  // 프로젝트 관리자 역할 부여
                .build();

        roleRepository.save(projectAdminRole);

        return new ProjectResponseDto(project);
    }

    @Override
    @Transactional
    public ProjectResponseDto updateProject(Long id, ProjectRequestDto projectRequestDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        // 사용자의 프로젝트 수정 권한 확인
        List<UserProjectRole> roles = roleRepository.findByUserEmailAndProjectId(email, id);
        if (roles.stream().noneMatch(role -> role.getRole() == RoleName.ROLE_PROJECT_ADMIN || role.getRole() == RoleName.ROLE_ADMIN)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);  // 권한 없음
        }

        project.setName(projectRequestDto.getName());
        project.setDescription(projectRequestDto.getDescription());
        return new ProjectResponseDto(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        // 사용자의 프로젝트 삭제 권한 확인
        List<UserProjectRole> roles = roleRepository.findByUserEmailAndProjectId(email, id);
        if (roles.stream().noneMatch(role -> role.getRole() == RoleName.ROLE_PROJECT_ADMIN || role.getRole() == RoleName.ROLE_ADMIN)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);  // 권한 없음
        }

        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDto> getUserProjects(String email) {
        List<UserProjectRole> userProjectRoles = roleRepository.findByUserEmail(email);

        return userProjectRoles.stream()
                .map(userProjectRole -> new ProjectResponseDto(userProjectRole.getProject()))
                .collect(Collectors.toList());
    }
}
