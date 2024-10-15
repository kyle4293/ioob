package com.ioob.backend.service;

import com.ioob.backend.entity.Project;
import com.ioob.backend.entity.RoleName;
import com.ioob.backend.entity.User;
import com.ioob.backend.entity.UserProjectRole;
import com.ioob.backend.exception.CustomException;
import com.ioob.backend.exception.ErrorCode;
import com.ioob.backend.repository.ProjectRepository;
import com.ioob.backend.repository.RoleRepository;
import com.ioob.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    
    @Transactional
    public void assignRoleToUser(Long projectId, String userEmail, RoleName roleName) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // 관리자 권한 확인
        if (!hasPermission(projectId, RoleName.ROLE_PROJECT_ADMIN, currentUserEmail)) {
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
                .role(roleName)
                .build();

        roleRepository.save(userProjectRole);
    }

    @Transactional(readOnly = true)
    
    public boolean hasPermission(Long projectId, RoleName requiredRole, String email) {
        List<UserProjectRole> roles = roleRepository.findByUserEmailAndProjectId(email, projectId);
        return roles.stream()
                .anyMatch(role -> role.getRole() == requiredRole || role.getRole() == RoleName.ROLE_ADMIN);
    }

    @Transactional(readOnly = true)
    
    public boolean isUserInProject(Long projectId, String email) {
        return !roleRepository.findByUserEmailAndProjectId(email, projectId).isEmpty();
    }
}
