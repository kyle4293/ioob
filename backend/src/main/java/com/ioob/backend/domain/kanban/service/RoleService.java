package com.ioob.backend.domain.kanban.service;

import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.auth.repository.UserRepository;
import com.ioob.backend.domain.kanban.entity.Project;
import com.ioob.backend.domain.kanban.entity.Role;
import com.ioob.backend.domain.kanban.entity.UserProjectRole;
import com.ioob.backend.domain.kanban.repository.ProjectRepository;
import com.ioob.backend.domain.kanban.repository.UserProjectRoleRepository;
import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RoleService {

    private final UserProjectRoleRepository userProjectRoleRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    
    @Transactional
    public void assignRoleToUser(Long projectId, String userEmail, Role role) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 관리자 권한 확인
        if (!hasPermission(projectId, Role.ROLE_PROJECT_ADMIN, currentUserEmail)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED);
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 권한 부여
        UserProjectRole userProjectRole = UserProjectRole.builder()
                .user(user)
                .project(project)
                .role(role)
                .build();

        userProjectRoleRepository.save(userProjectRole);
    }

    @Transactional(readOnly = true)
    public boolean hasPermission(Long projectId, Role requiredRole, String email) {
        List<UserProjectRole> roles = userProjectRoleRepository.findByUserEmailAndProjectId(email, projectId);
        return roles.stream()
                .anyMatch(role -> role.getRole() == requiredRole || role.getRole() == Role.ROLE_ADMIN);
    }

    @Transactional(readOnly = true)
    public boolean isUserInProject(Long projectId, String email) {
        return !userProjectRoleRepository.findByUserEmailAndProjectId(email, projectId).isEmpty();
    }
}
