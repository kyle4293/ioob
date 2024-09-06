package com.ioob.backend.service.impl;

import com.ioob.backend.entity.Project;
import com.ioob.backend.entity.RoleName;
import com.ioob.backend.entity.User;
import com.ioob.backend.entity.UserProjectRole;
import com.ioob.backend.exception.CustomException;
import com.ioob.backend.exception.ErrorCode;
import com.ioob.backend.repository.ProjectRepository;
import com.ioob.backend.repository.RoleRepository;
import com.ioob.backend.repository.UserRepository;
import com.ioob.backend.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository; // 기존 UserProjectRoleRepository
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public void assignRoleToUser(Long projectId, Long userId, RoleName roleName) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!checkAdminPermission(projectId, currentUserEmail)) {
            throw new CustomException(ErrorCode.AUTHORIZATION_REQUIRED); // 권한 없음
        }

        // 권한 부여 로직
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        UserProjectRole userProjectRole = UserProjectRole.builder()
                .user(user)
                .project(project)
                .role(roleName)
                .build();

        roleRepository.save(userProjectRole);
    }


    @Transactional(readOnly = true)
    @Override
    public boolean checkAdminPermission(Long projectId, String email) {
        List<UserProjectRole> roles = roleRepository.findByUserEmailAndProjectId(email, projectId);
        return roles.stream()
                .anyMatch(role -> role.getRole() == RoleName.ROLE_PROJECT_ADMIN || role.getRole() == RoleName.ROLE_ADMIN);
    }


    @Transactional(readOnly = true)
    @Override
    public boolean isUserInProject(Long projectId, String email) {
        // 사용자가 프로젝트에 속해 있는지 확인
        return !roleRepository.findByUserEmailAndProjectId(email, projectId).isEmpty();
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isUserProjectAdmin(Long projectId, String email) {
        // 프로젝트 관리자 권한 여부 확인
        List<UserProjectRole> roles = roleRepository.findByUserEmailAndProjectId(email, projectId);
        return roles.stream().anyMatch(role -> role.getRole() == RoleName.ROLE_PROJECT_ADMIN);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasPermission(Long projectId, RoleName requiredRole, String email) {
        List<UserProjectRole> roles = roleRepository.findByUserEmailAndProjectId(email, projectId);
        return roles.stream()
                .anyMatch(role -> role.getRole() == requiredRole || role.getRole() == RoleName.ROLE_ADMIN);
    }
}
